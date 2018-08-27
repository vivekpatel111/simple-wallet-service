package com.wallet.api;

import java.lang.reflect.Type;
import java.util.Date;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import spark.ResponseTransformer;

public class JsonTransformer implements ResponseTransformer 
{
    @SuppressWarnings("rawtypes")
    class DateSerializer implements JsonSerializer 
    {
        @Override
        public JsonElement serialize(Object src, Type typeOfSrc,
                                     JsonSerializationContext context)
        {
            Date date = (Date) src;
            return date == null ? null : new JsonPrimitive(date.getTime());
        }
    }
    
    private Gson gson;
    
    public JsonTransformer()
    {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Date.class, new DateSerializer());
        
        this.gson = gsonBuilder.create();
    }
    
    @Override
    public String render(Object model) 
    {
        return gson.toJson(model);
    }
}
