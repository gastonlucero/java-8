package com.juanko.core;

import com.juanko.core.action.CommandAction;
import com.juanko.core.action.QueryAction;
import com.juanko.core.action.command.CommandActionTask;
import com.juanko.core.action.command.CommandAsyncActionTask;
import com.juanko.core.action.command.CommandController;
import com.juanko.core.action.query.QueryActionTask;
import com.juanko.core.action.query.QueryController;
import com.juanko.core.annotations.CommandActionHandler;
import com.juanko.core.annotations.QueryActionHandler;
import com.juanko.core.annotations.WebPath;
import com.juanko.core.dao.DaoManager;
import com.juanko.core.dao.PublicDaoManager;
import com.juanko.core.data.model.RepresentationModel;
import com.juanko.core.data.nosql.NoSqlDataConnection;
import com.juanko.core.data.nosql.NoSqlSource;
import com.juanko.core.data.nosql.mongodb.ReflectionUtils;
import com.juanko.core.data.utils.ResourcesManager;
import com.juanko.core.exceptions.ExecutionException;
import com.juanko.core.gateway.CommandCoreInterface;
import com.juanko.core.gateway.CommandGateway;
import com.juanko.core.gateway.QueryCoreInterface;
import com.juanko.core.gateway.QueryGateway;
import com.juanko.core.integration.message.JmsEndPointRoute;
import com.juanko.core.integration.message.MessageFactory;
import com.juanko.core.integration.web.RestContext;
import com.juanko.core.listener.event.SeagalEvent;
import com.juanko.core.proxy.CoreGateway;
import java.io.File;
import java.io.FileInputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;
import org.apache.log4j.PropertyConfigurator;

/**
 *
 * @author gaston
 */
public class Core<R extends RepresentationModel> extends Observable
        implements CoreBase, QueryCoreInterface<R>, CommandCoreInterface<R> {

    private ExecutorService commandExecutor;
    private ExecutorService queryExecutor;
    private Map<Class, Map<Integer, CommandController>> comandActionControllers;
    private Map<Class, QueryController> queryActionControllers;
    private Map<String, NoSqlSource> noSqlSources;
    private Map<Class, DaoManager> daos;

    private static final List<SeagalEvent> pendingEvents = new LinkedList();

    public void initBusinessCore(int commandPoolSize, int queryPoolSize) throws Exception {
        PropertyConfigurator.configure(new FileInputStream(new File(
                "etc" + System.getProperty("file.separator") + "log4j-" + System.getenv("TSENV") + ".properties")));
        commandExecutor = Executors.newFixedThreadPool(commandPoolSize);
        queryExecutor = Executors.newFixedThreadPool(queryPoolSize);
        comandActionControllers = new HashMap<>();
        queryActionControllers = new HashMap<>();
        noSqlSources = new HashMap<>();
        daos = new HashMap<>();
        processEvents();
        initGateways();
        this.setChanged();
        this.notifyObservers("Iniciado");
        initDaoManagers();
        initControllers();
        mappingEntities();
        startShutdownHook();
    }

    @Override
    public synchronized void addObserver(Observer o) {
        super.addObserver(o);
    }

    public void initGateways() {
        this.addObserver(new CoreGateway());
        this.addObserver(new CommandGateway());
        this.addObserver(new QueryGateway());
    }

    public NoSqlSource initNoSqlSource(Class dataSource) throws Exception {
        Constructor constr = dataSource.getConstructor();
        NoSqlSource noSqlSource = (NoSqlSource) constr.newInstance();
        noSqlSources.putIfAbsent(noSqlSource.getClass().getSimpleName().toLowerCase(), noSqlSource);
        ResourcesManager.addResourceObserver(noSqlSource);
        return noSqlSource;
    }

    @Override
    public void addDaoManagers() {
        String packageBase = this.getClass().getPackage().getName().replace("/", ".");
        URL resource = this.getClass().getClassLoader().getResource(packageBase.replace(".", "/"));
        scanningAnnotatedClasses(new File(resource.getFile().replace("%20", " ")), packageBase, DaoManager.class, null)
                .stream().forEach((clazz) -> {
                    addDao(clazz);
                });
    }

    @Override
    public void addControllers() {
        String packageBase = this.getClass().getPackage().getName().replace("/", ".");
        URL resource = this.getClass().getClassLoader().getResource(packageBase.replace(".", "/"));
        scanningAnnotatedClasses(new File(resource.getFile().replace("%20", " ")), packageBase, QueryController.class, QueryActionHandler.class)
                .stream().forEach((clazz) -> {
                    addQueryController(clazz);
                });
        scanningAnnotatedClasses(new File(resource.getFile().replace("%20", " ")), packageBase, CommandController.class, CommandActionHandler.class)
                .stream().forEach((clazz) -> {
                    addCommandController(clazz);
                });
    }

    @Override
    public void addEntities() {
        String packageBase = this.getClass().getPackage().getName().replace("/", ".");
        URL resource = this.getClass().getClassLoader().getResource(packageBase.replace(".", "/"));
        scanningAnnotatedClasses(new File(resource.getFile().replace("%20", " ")), packageBase, RepresentationModel.class, null)
                .stream().forEach((clazz) -> {
                    ReflectionUtils.addEntity(clazz);
                });
    }

    @Override
    public void addWebContext() {
        String packageBase = this.getClass().getPackage().getName().replace("/", ".");
        URL resource = this.getClass().getClassLoader().getResource(packageBase.replace(".", "/"));
        scanningAnnotatedClasses(new File(resource.getFile().replace("%20", " ")), packageBase, RestContext.class, WebPath.class)
                .stream().forEach((clazz) -> {
                    try {
                        clazz.newInstance();
                    } catch (Exception e) {
                    }
                });
    }

    @Override
    public void addMessageEndPoint() {
        String packageBase = this.getClass().getPackage().getName().replace("/", ".");
        URL resource = this.getClass().getClassLoader().getResource(packageBase.replace(".", "/"));
        scanningAnnotatedClasses(new File(resource.getFile().replace("%20", " ")), packageBase, JmsEndPointRoute.class, null)
                .stream().forEach((clazz) -> {
                    try {
                        clazz.newInstance();
                    } catch (Exception e) {
                    }
                });
    }

    public void startMessageEndPoints() {
        MessageFactory.start();
    }

    private List<Class> scanningAnnotatedClasses(File directory, String packageName, Class clazzToEvaluate, Class annotationToEvaluate) {
        List<Class> classes = new ArrayList<>();
        if (!directory.exists()) {
            return classes;
        }
        File[] files = directory.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                classes.addAll(scanningAnnotatedClasses(file, packageName + "." + file.getName(), clazzToEvaluate, annotationToEvaluate));
            } else if (file.getName().endsWith(".class")) {
                try {
                    //-6 para restar .class
                    Class clazz = Class.forName(packageName + '.'
                            + file.getName().substring(0, file.getName().length() - 6));

                    if (clazzToEvaluate.isAssignableFrom(clazz) || (annotationToEvaluate != null && clazz.isAnnotationPresent(annotationToEvaluate))) {
                        classes.add(clazz);
                    }
                } catch (ClassNotFoundException e) {
                }
            }
        }
        return classes;
    }

    protected void addDao(Class daoClass, DaoManager instance) {
        daos.put(daoClass, instance);
    }

    private void addDao(Class daoClass) {
        try {
            Optional<Class> daoInterface = Stream.of(daoClass.getInterfaces()).filter(interfaceClazz
                    -> PublicDaoManager.class.isAssignableFrom(interfaceClazz)).findFirst();
            daoInterface.ifPresent((interfaceClazz) -> {
                try {
                    addDao(interfaceClazz, (DaoManager) daoClass.newInstance());
                } catch (IllegalAccessException | InstantiationException e) {
                    throw new ExecutionException("Error al instanciar clase" + daoClass.getName(), e);
                }
            });
        } catch (Exception e) {
            throw new ExecutionException("Error al agregar daoManager " + daoClass.getName(), e);
        }
    }

    protected void addQueryController(Class controller, QueryController instance) {
        queryActionControllers.putIfAbsent(((QueryActionHandler) controller.getAnnotation(QueryActionHandler.class)).queryAction(),
                instance);
    }

    protected void addCommandController(Class controller, CommandController instance) {
        for (Annotation annotation : controller.getAnnotationsByType(CommandActionHandler.class)) {
            Class commandAction = ((CommandActionHandler) annotation).commandAction();
            if (comandActionControllers.get(commandAction) == null) {
                comandActionControllers.put(commandAction, new HashMap<>());
            }
            comandActionControllers.get(commandAction).putIfAbsent(((CommandActionHandler) annotation).order(),
                    instance);
        }
    }

    private void addQueryController(Class controller) {
        try {
            addQueryController(controller, (QueryController) controller.newInstance());
        } catch (IllegalAccessException | InstantiationException e) {
            throw new ExecutionException(e.getMessage(), e);
        }
    }

    private void addCommandController(Class controller) {
        try {
            for (Annotation annotation : controller.getAnnotationsByType(CommandActionHandler.class)) {
                Class commandAction = ((CommandActionHandler) annotation).commandAction();
                if (comandActionControllers.get(commandAction) == null) {
                    comandActionControllers.put(commandAction, new HashMap<>());
                }
                comandActionControllers.get(commandAction).putIfAbsent(((CommandActionHandler) annotation).order(),
                        (CommandController) controller.newInstance());
            }
        } catch (IllegalAccessException | InstantiationException e) {
            throw new ExecutionException(e.getMessage(), e);
        }
    }

    @Override
    public Stream<CommandController> getCommandsController(Class commandActionClass) {
        //Hacerlo ordenado  
        if (!comandActionControllers.containsKey(commandActionClass)) {
            throw new ExecutionException("No existe CommandController asociado a " + commandActionClass.getCanonicalName());
        }
        return comandActionControllers.get(commandActionClass).values().stream();
    }

    @Override
    public QueryController getQueryController(Class queryActionClass) throws ExecutionException {
        if (!queryActionControllers.containsKey(queryActionClass)) {
            throw new ExecutionException("No existe QueryController asociado a " + queryActionClass.getCanonicalName());
        }
        return queryActionControllers.get(queryActionClass);
    }

    @Override
    public R executeQueryAction(QueryAction action) throws ExecutionException {
        try {
            return (R) CompletableFuture.supplyAsync(new QueryActionTask(action), queryExecutor)
                    .exceptionally((exception) -> {
                        return null;
                    })
                    .join();
        } catch (CompletionException | ExecutionException e) {
            throw new ExecutionException("Error al ejectuar el queryAction " + action.toString());
        }
    }

    @Override
    public void executeCommandAction(CommandAction action, boolean transacted) throws ExecutionException {
        try {
            CompletableFuture.runAsync(new CommandAsyncActionTask(action), commandExecutor)
                    .exceptionally((exception) -> {
                        //deberia hacer algo aca, lanzar una action para algo?
                        return null;
                    }).join();
        } catch (Exception e) {
            if (e instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
            throw new ExecutionException("Error al ejectuar el commandAction " + action.toString());
        }
    }

    @Override
    public R executeAndGetCommandAction(CommandAction action, boolean transacted) {
        try {
            return (R) CompletableFuture.supplyAsync(new CommandActionTask(action), commandExecutor)
                    .exceptionally((exception) -> {
                        return null;
                    })
                    .join();
        } catch (CompletionException | ExecutionException e) {
            throw new ExecutionException("No existe CommandController asociado a " + action.getClass().getCanonicalName(), e);
        }
    }

    @Override
    public NoSqlDataConnection getNoSqlConnection(String noSqlSourceName) throws ExecutionException {
        return noSqlSources.get(noSqlSourceName).getConnection();
    }

    @Override
    public <P extends PublicDaoManager> P getDaoManager(Class daoManager) throws ExecutionException {
        return (P) daos.get(daoManager);
    }

    public static <E extends SeagalEvent> void sendEvent(E event) {
        pendingEvents.add(event);
        synchronized (pendingEvents) {
            pendingEvents.notify();
        }
    }

    @Override
    public <E extends SeagalEvent> void processEvents() {
        new Thread(
                () -> {
                    synchronized (pendingEvents) {
                        while (pendingEvents.isEmpty()) {
                            try {
                                pendingEvents.wait();
                            } catch (InterruptedException e) {
                            }
                        }
                        SeagalEvent event = pendingEvents.remove(0);
                        event.handleEvent();
                    }
                }).start();
    }

    private void schedulerTasks() {

    }

    /**
     * Metodo encargado de ejecutarse al momento de una interrupcion del proceso
     * java, que destruye todo lo referido al contexto instanciado, ejecuta el
     * metodo finalize en los hilos que todavia no lo han ejecutado, y elimina
     * la instancia de la jvm
     */
    private void startShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                try {
                    commandExecutor.shutdown();
                    commandExecutor.awaitTermination(1, TimeUnit.MINUTES);
                    queryExecutor.shutdown();
                    queryExecutor.awaitTermination(1, TimeUnit.MINUTES);
                    System.runFinalization();
                    System.gc();
                    Thread.sleep(5000);
                } catch (InterruptedException ex) {

                } finally {
                    Runtime.getRuntime().halt(0);
                }
            }
        });
    }

}
