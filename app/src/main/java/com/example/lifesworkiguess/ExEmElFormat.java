package com.example.lifesworkiguess;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

public class ExEmElFormat extends AppCompatActivity {

EditText nameET, ingredientsET, stepsET;
String name, ingredients, steps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ex_em_el_format);

        nameET = (EditText) findViewById(R.id.dishName);
        ingredientsET = findViewById(R.id.Ingredients);
        stepsET = findViewById(R.id.Steps);


    }

    public void makeXML(View view) throws ParserConfigurationException, TransformerException {
        name = nameET.getText().toString();
        ingredients = ingredientsET.getText().toString();
        steps = stepsET.getText().toString();
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

        // root elements
        Document doc = docBuilder.newDocument();
        Element rootElement = doc.createElement("Recipe" + name);
        doc.appendChild(rootElement);

        Element ingredientsE = doc.createElement("Ingredients");
        rootElement.appendChild(ingredientsE);
        Attr actualIngredients = doc.createAttribute("actualIngredients");
        actualIngredients.setValue(ingredients);
        ingredientsE.setAttributeNode(actualIngredients);

        Element stepsE = doc.createElement("Steps");
        rootElement.appendChild(stepsE);
        Attr actualSteps = doc.createAttribute("actualSteps");
        actualSteps.setValue(steps);
        stepsE.setAttributeNode(actualSteps);

        //...create XML elements, and others...

        // write dom document to a file
        try (FileOutputStream output =
                     new FileOutputStream("C:\\Users\\bensv\\Desktop\\recipe.xml")) {
            writeXml(doc, output);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static void writeXml(Document doc,
                                 OutputStream output)
            throws TransformerException {

        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(output);

        transformer.transform(source, result);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.main, menu);
        menu.removeItem(R.id.XMLScreen);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item){
        if (item.getTitle().toString().equals("User Authentication") ){
            Intent si = new Intent(this, MainActivity.class);
            startActivity(si);
        }
        if (item.getTitle().toString().equals("Gallary Choose") ){
            Intent si = new Intent(this, GallaryChoose.class);
            startActivity(si);
        }
        if (item.getTitle().toString().equals("Camera") ){
            Intent si = new Intent(this, CameraUpload.class);
            startActivity(si);
        }
        if (item.getTitle().toString().equals("Notification") ){
            Intent si = new Intent(this, NotificationScreen.class);
            startActivity(si);
        }
        if (item.getTitle().toString().equals("Time Picker") ){
            Intent si = new Intent(this, TimePickerToast.class);
            startActivity(si);
        }


        return true;
    }

}