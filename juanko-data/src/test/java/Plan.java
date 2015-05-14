
import com.juanko.core.data.annotation.FieldColumn;
import com.juanko.core.data.annotation.TableOrCollection;
import com.juanko.core.data.model.Entity;
import java.util.function.Consumer;

/**
 *
 * @author gaston
 */
@TableOrCollection(name = "atributo_plan")
public class Plan extends Entity {

    @FieldColumn(name = "id")
    private Long id;
    @FieldColumn(name = "key_nombre")
    private String key;
    @FieldColumn(name = "valor_default")
    private String defaultValue;
    @FieldColumn(name = "tipo_atributo")
    private String type;

    public Plan() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
         Consumer<String> f = (s) -> System.out.println(s);
        f.accept(id+" "+defaultValue+" "+key+" "+type);
        return id+" "+defaultValue+" "+key+" "+type;
    }

    
}
