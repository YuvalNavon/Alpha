package com.example.lifesworkiguess;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Documented;
import java.util.ArrayList;

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


    public static Recipe XMLToRecipe(Context context, String fileName){
        String recipeName = "ERROR";
        Recipe recipe = new Recipe(recipeName);
        ArrayList<Ingredient> ingsList = new ArrayList<>();
        ArrayList<Step> stepsList = new ArrayList<>();
        ArrayList<String> actionsList = new ArrayList<>();
        if (isFileExists(context, fileName)) {
            File file = new File(context.getExternalFilesDir("MyDir"), fileName);
            try {
                FileInputStream fis = new FileInputStream(file);
                XmlPullParser parser = XmlPullParserFactory.newInstance().newPullParser();
                parser.setInput(fis, null);
                int event = parser.getEventType();
                while (event != XmlPullParser.END_DOCUMENT) {
                    if (event == XmlPullParser.START_TAG) {

                        if (parser.getAttributeCount()== 1){
                            recipeName = parser.getAttributeValue(0);
                            recipe.setTitle(recipeName);

                        }
                        if (parser.getName().equals("Ingredient")){
                            Ingredient ingredient = new Ingredient(parser.getAttributeValue(0),
                                    parser.getAttributeValue(1), parser.getAttributeValue(2));
                            ingsList.add(ingredient);


                        }
                        if (parser.getName().equals("Step")){


                            Step step = new Step(parser.getAttributeValue(0), parser.getAttributeValue(1),
                                    parser.getAttributeValue(2), parser.getAttributeValue(3));
                            stepsList.add(step);

                        }


                    }
                    event = parser.next();
                }
                for (int i=0; i<ingsList.size();i++){
                    recipe.addIngredient(ingsList.get(i));

                }
                for (int i=0; i<stepsList.size();i++){
                    recipe.addStep(stepsList.get(i));

                }


            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return recipe;
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
            //StepName
            Attr stepName = doc.createAttribute("Name");
            stepName.setValue(recipe.getSteps().get(i).getName());
            //StepDescription
            Attr stepDescription = doc.createAttribute("Description");
            stepDescription.setValue(recipe.getSteps().get(i).getDescription());
            //StepTime
            Attr stepTime = doc.createAttribute("Time");
            stepTime.setValue(recipe.getSteps().get(i).getTime());
            //StepAction
            Attr stepAction = doc.createAttribute("Action");
            stepAction.setValue(recipe.getSteps().get(i).getAction());
            //stepNumber
            Attr stepNumber = doc.createAttribute("Number");
            stepNumber.setValue(String.valueOf(recipe.getSteps().get(i).getNumber()));
            //Setting Attr and appending
            stepE.setAttributeNode(stepName);
            stepE.setAttributeNode(stepDescription);
            stepE.setAttributeNode(stepTime);
            stepE.setAttributeNode(stepAction);
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


    //This is the one that actually makes the file available in the app's data that you can see in Android Studio!!
    //As long as you use DownloadFiles, the file is in the phone's data anyway.
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

        StorageReference fStorage = FirebaseStorage.getInstance().getReference("Community Recipes");
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



    public static  void downloadXML(Context context, String fileName, String path){
        //fileName MUST BE WITH THE EXTENSION
        StorageReference fStorage = FirebaseStorage.getInstance().getReference(path);
        StorageReference fileRef = fStorage.child(fileName);
        fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                String strUrl = uri.toString();
                Toast.makeText(context, "Downloading...", Toast.LENGTH_LONG).show();
                downloadFiles(context, MyConstants.CURRENTLY_LEARNED_RECIPE,"MyDir" , strUrl);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, "Recipe Not Found!", Toast.LENGTH_LONG).show();

            }
        });
    }

    public static void downloadFiles(Context context, String fileName, String destinationDirectory, String url){
        if (isFileExists(context, fileName))
        {
            deleteFile(context, fileName);
        }

        DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        Uri uri = Uri.parse(url);
        DownloadManager.Request request = new DownloadManager.Request(uri);

        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalFilesDir(context, destinationDirectory, fileName);

        downloadManager.enqueue(request);
    }
    public static boolean isFileExists(Context context, String filename){


        File folder1 = new File(context.getExternalFilesDir("MyDir"), filename);
        return folder1.exists();

    }

    public static boolean deleteFile(Context context, String filename) {

        File folder1 = new File(context.getExternalFilesDir("MyDir"), filename);
        return folder1.delete();
    }




}
