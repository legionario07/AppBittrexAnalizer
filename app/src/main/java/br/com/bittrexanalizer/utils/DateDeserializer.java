package br.com.bittrexanalizer.utils;

import android.util.Log;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by PauLinHo on 10/09/2017.
 */

public class DateDeserializer implements JsonDeserializer<Date>, JsonSerializer<Date> {

    private SimpleDateFormat JSON_STRING_DATE;

    public DateDeserializer(SimpleDateFormat sdf){
        this.JSON_STRING_DATE = sdf;
    }

    @Override
    public Date deserialize(JsonElement je, Type type, JsonDeserializationContext jdc) throws JsonParseException {

        try {
            return JSON_STRING_DATE.parse(je.getAsString());
        } catch (ParseException ex) {
            Log.i("BITTREX", ex.getMessage());
        }
        return null;
    }

    @Override
    public JsonElement serialize(Date t, Type type, JsonSerializationContext jsc) {

        String data = JSON_STRING_DATE.format(t);

        return new JsonPrimitive(data);
    }
}
