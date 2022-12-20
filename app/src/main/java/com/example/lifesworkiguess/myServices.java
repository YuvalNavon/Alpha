package com.example.lifesworkiguess;

import android.content.Context;
import android.net.Uri;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Documented;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

public class myServices {

    public static void writeXml(Document doc,
                         OutputStream output)
            throws TransformerException {


        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();

        transformer.setOutputProperty(OutputKeys.INDENT, "yes");

        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(output);

        transformer.transform(source, result);


    }

    public static void uploadXML(Context context, String fileName){

        StorageReference fStorage = FirebaseStorage.getInstance().getReference("Recipes");
        String filesDir = context.getFilesDir().getPath();
        String XMLFilePath = filesDir + "/" + fileName + ".xml";
        File XMLFile = new File(XMLFilePath);
        Uri XMLUri = Uri.fromFile(XMLFile);
        if (XMLUri!=null){

            StorageReference fRef = fStorage.child("Recipe For " + fileName + ".xml");

            fRef.putFile(XMLUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(context, "Recipe Uploaded!", Toast.LENGTH_LONG).show();

                }
            });
        }
        else
        {
            Toast.makeText(context, "Error, File was not Uploaded", Toast.LENGTH_LONG).show();
        }
    }

    public static void recipeToXML(Context context, Recipe recipe, String fileName){
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = null;
        try {
            docBuilder = docFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }

        // root elements
        Document doc = docBuilder.newDocument();
        Element rootElement = doc.createElement("Recipe");
        doc.appendChild(rootElement);
        Element nameE = doc.createElement("Title");
        Attr actualName = doc.createAttribute("Recipe_Title");
        actualName.setValue(recipe.getTitle());
        nameE.setAttributeNode(actualName);
        rootElement.appendChild(nameE);


        //Adding Ingredients
        Element ingredientsListE = doc.createElement("Ingredients_List");
        rootElement.appendChild(ingredientsListE);

        for (int i = 0; i<recipe.getIngredients().size(); i++){
            Element ingredientE = doc.createElement("Ingredient");
            Attr ingredientName = doc.createAttribute("Name");
            ingredientName.setValue(recipe.getIngredients().get(i).getName());
            Attr ingredientAmount = doc.createAttribute("Amount");
            ingredientAmount.setValue(recipe.getIngredients().get(i).getAmount());
            Attr ingredientUnits = doc.createAttribute("Units");
            ingredientUnits.setValue(recipe.getIngredients().get(i).getUnits());
            ingredientE.setAttributeNode(ingredientName);
            ingredientE.setAttributeNode(ingredientAmount);
            ingredientE.setAttributeNode(ingredientUnits);
            ingredientsListE.appendChild(ingredientE);
        }


        //Adding Steps
        Element stepsListE = doc.createElement("Steps_List");
        rootElement.appendChild(stepsListE);
        for (int i =0; i<recipe.getSteps().size();i++){
            Element stepE = doc.createElement("Step");
            Attr stepName = doc.createAttribute("Name");
            stepName.setValue(recipe.getSteps().get(i).getName());
            Attr stepDescription = doc.createAttribute("Description");
            stepDescription.setValue(recipe.getSteps().get(i).getDescription());
            Attr stepNumber = doc.createAttribute("Number");
            stepNumber.setValue(String.valueOf(recipe.getSteps().get(i).getNumber()));
            stepE.setAttributeNode(stepName);
            stepE.setAttributeNode(stepDescription);
            stepE.setAttributeNode(stepNumber);
            stepsListE.appendChild(stepE);

        }





        // write dom document to a file
        try (FileOutputStream output =
                     new FileOutputStream(context.getFilesDir().getPath() + "/"  + fileName + ".xml")) {
            writeXml(doc, output);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TransformerException e) {
            e.printStackTrace();
        }

    }
}
