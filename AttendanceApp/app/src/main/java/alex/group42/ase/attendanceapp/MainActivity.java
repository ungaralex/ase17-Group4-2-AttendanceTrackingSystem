package alex.group42.ase.attendanceapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import org.restlet.resource.ClientResource;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

public class MainActivity extends AppCompatActivity {

    private GoogleSignInClient mSignInClient = null;
    private static final int RC_SIGN_IN = 1337;
    private static final String TAG = "AttendanceApp.Main";
    private String eMailFromAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // INET ACCESS
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        // END ACCESS

        //Google SignIn Config
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mSignInClient = GoogleSignIn.getClient(this, gso);

        Button buttonOne = findViewById(R.id.bRequestQR);
        buttonOne.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                generateQRCode();
            }
        });

        SignInButton signInButton = findViewById(R.id.sign_in_button);
        signInButton.setOnClickListener(new SignInButton.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        updateUI(account);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.w(TAG,"Called onActivityResult()");
        Log.w(TAG,"Request code is " + requestCode);
        super.onActivityResult(requestCode, resultCode, data);

        //For sign in
        if(requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> task) {
        Log.w(TAG,"Called handleSignInResult()");
        try {
            GoogleSignInAccount account = task.getResult(ApiException.class);
            this.eMailFromAccount = account.getEmail();
            Log.w(TAG,"Account mail is " + this.eMailFromAccount);
            updateUI(account);
        } catch(ApiException e) {
            Log.w(TAG, "signInResult:SignIn failed with code="+e.getStatusCode());
            updateUI(null);
        }
    }

    private void signIn() {
        Log.w(TAG, "Called signIn()");
        Intent signInIntent = mSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }


    /**
     * This method changes the Activity's UI appearance based on the account status
     * @param account A GoogleSignInAccount for this Activity. If this is non-null,
     *                we assume successful login and change the UI appearance.
     */
    private void updateUI(GoogleSignInAccount account) {
        if(account == null) {
            return;
        }
        else {
            //Change visibility of UI elements if account is present
            Button qrBtn = findViewById(R.id.bRequestQR);
            SignInButton signInButton = findViewById(R.id.sign_in_button);
            TextView signInText = findViewById(R.id.signInText);
            ImageView qrImg = findViewById(R.id.imageView);

            signInButton.setVisibility(View.GONE);
            signInText.setVisibility(View.GONE);
            qrBtn.setVisibility(View.VISIBLE);
            qrImg.setVisibility(View.VISIBLE);
        }
    }

    private void generateQRCode() {
        String urlGetStudent = "http://ase-2017-alex.appspot.com/rest/student/";
        String urlGetToken = "http://ase-2017-alex.appspot.com/rest/tokens/";

        // TODO get gmail address
        String accountName = getEmail();

        try {
            TextView infoOut = findViewById(R.id.debugText);

            Document student = parseXMLString(new ClientResource(urlGetStudent + accountName).get().getText());
            String studentId = student.getElementsByTagName("studentId").item(0).getTextContent();
            if (studentId.equals("NOT_FOUND")) {
                infoOut.setText("You are not registered!");
                return;
            }

            Document token = parseXMLString(new ClientResource(urlGetToken + studentId).get().getText());
            String tokenString = token.getElementsByTagName("token").item(0).getTextContent();
            if (tokenString.equals("WRONG_DATE")) {
                infoOut.setText("This is not your timeslot!");
                return;
            }

            infoOut.setText("");

            // http://www.java2s.com/Tutorials/Java/XML/How_to_convert_org_w3c_dom_Document_to_String.htm
            Document attendanceXML = createAttendanceXML(studentId, tokenString);
            DOMSource domSource = new DOMSource(attendanceXML);
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            StringWriter sw = new StringWriter();
            StreamResult sr = new StreamResult(sw);
            transformer.transform(domSource, sr);

            // https://stackoverflow.com/questions/8800919/how-to-generate-a-qr-code-for-an-android-application
            QRCodeWriter writer = new QRCodeWriter();
            BitMatrix bitMatrix = writer.encode(sw.toString(), BarcodeFormat.QR_CODE, 512, 512);
            int width = bitMatrix.getWidth();
            int height = bitMatrix.getHeight();
            Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    bmp.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                }
            }
            ((ImageView) findViewById(R.id.imageView)).setImageBitmap(bmp);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerException e) {
            e.printStackTrace();
        } catch (WriterException e) {
            e.printStackTrace();
        }

    }

    private String getEmail() {
        if (this.eMailFromAccount.isEmpty()) {
            return null;
        }
        else {
            return this.eMailFromAccount;
        }
    }

    private Document parseXMLString(String xmlString) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder;
        Document doc = null;

        try {
            documentBuilder =   factory.newDocumentBuilder();
            doc = documentBuilder.parse(new InputSource(new StringReader(xmlString)));
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return doc;
    }

    private Document createAttendanceXML(String studentId, String token) {
        Document doc = null;
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder;

        try {
            documentBuilder = factory.newDocumentBuilder();
            doc = documentBuilder.newDocument();
            doc.setXmlVersion("1.0");

            Element attendance = doc.createElement("attendance");
            Element sId = doc.createElement("studentId");
            sId.appendChild(doc.createTextNode(studentId));
            Element tok = doc.createElement("token");
            tok.appendChild(doc.createTextNode(token));

            attendance.appendChild(sId);
            attendance.appendChild(tok);

            doc.appendChild(attendance);
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }

        return doc;
    }
}
