package com.juanko.core.data.parser;

/**
 *
 * @author gaston
 */
public final class RepresentationParserFactory {

    private static RepresentationParserFactory instance ;
    
    public static RepresentationParserFactory getInstance(){
        if(instance==null){
            instance=new RepresentationParserFactory();
        }
        return instance;
    }
    public SeagalJsonParser getJsonParser() {
        return new SeagalJsonParser();
    }
}
