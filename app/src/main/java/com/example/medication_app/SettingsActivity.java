package com.example.medication_app;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.hardware.SensorManager;
import android.net.Uri;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;

public class SettingsActivity extends AppCompatActivity {

    Button contactsButton;
    Button nfcButton;
    NfcAdapter mNfcAdapter;
    public static final String MIME_TEXT_PLAIN = "text/plain";
    public final int PICK_CONTACT = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);

        contactsButton = (Button) findViewById(R.id.contactButton);
        nfcButton = (Button) findViewById(R.id.nfcButton);

        FileInputStream fileInputStream = null;
        String emergencyContactFileText = "";

        try {
            fileInputStream = openFileInput("emergencyContact.txt");
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);

            char[] buffer = new char[1024];
            int charRead;

            //creates string that contains text from 'emergencyContact.txt'
            while ((charRead = inputStreamReader.read(buffer)) > 0) {
                String readString = String.copyValueOf(buffer, 0, charRead);
                emergencyContactFileText += readString;
            }

            String buttonText = "         Emergency Contact Set to: " + emergencyContactFileText;
            contactsButton.setText(buttonText);

        } catch (Exception e) {
            e.printStackTrace();
        }

        String nfcIDFileText = "";

        try {
            fileInputStream = openFileInput("nfcID.txt");
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);

            char[] buffer = new char[1024];
            int charRead;

            //creates string that contains text from 'nfcID.txt'
            while ((charRead = inputStreamReader.read(buffer)) > 0) {
                String readString = String.copyValueOf(buffer, 0, charRead);
                nfcIDFileText += readString;
            }

            String buttonText = "         NFC ID: " + nfcIDFileText;
            nfcButton.setText(buttonText);

        } catch (Exception e) {
            e.printStackTrace();
        }


        contactsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
                startActivityForResult(i, PICK_CONTACT);
            }
        });

        handleIntent(getIntent());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == PICK_CONTACT)
        {
            Uri contactUri = data.getData();
            Cursor cursor = getContentResolver().query(contactUri, null, null, null, null);
            cursor.moveToFirst();
            int column = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
            String buttonText = "         Emergency Contact Set to: " + cursor.getString(column);
            contactsButton.setText(buttonText);
            updateContact(cursor.getString(column));
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }

    public static void setupForegroundDispatch(Activity activity, NfcAdapter adapter) {
        final Intent intent = new Intent(activity.getApplicationContext(), activity.getClass());
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        final PendingIntent pendingIntent = PendingIntent.getActivity(activity.getApplicationContext(), 0, intent, 0);

        IntentFilter[] filters = new IntentFilter[1];
        String[][] techList = new String[][]{};

        // Notice that this is the same filter as in our manifest.
        filters[0] = new IntentFilter();
        filters[0].addAction(NfcAdapter.ACTION_NDEF_DISCOVERED);
        filters[0].addCategory(Intent.CATEGORY_DEFAULT);
        try {
            filters[0].addDataType(MIME_TEXT_PLAIN);
        } catch (IntentFilter.MalformedMimeTypeException e) {
            throw new RuntimeException("Check your mime type.");
        }

        adapter.enableForegroundDispatch(activity, pendingIntent, filters, techList);
    }

    public static void stopForegroundDispatch(final Activity activity, NfcAdapter adapter) {
        adapter.disableForegroundDispatch(activity);
    }

    private void handleIntent(Intent intent) {
        String action = intent.getAction();
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {

            String type = intent.getType();
            if (MIME_TEXT_PLAIN.equals(type)) {

                Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);

                updateNFCID(getTextFromTag(tag));

                String buttonText = "         NFC ID: " + getTextFromTag(tag);
                nfcButton.setText(buttonText);
            }
        }

        else {
                //keep on going
        }
    }


    protected String getTextFromTag(Tag tag) {
        Ndef ndef = Ndef.get(tag);
        if (ndef == null) {
            // NDEF is not supported by this Tag.
            return null;
        }

        NdefMessage ndefMessage = ndef.getCachedNdefMessage();

        NdefRecord[] records = ndefMessage.getRecords();
        for (NdefRecord ndefRecord : records) {
            if (ndefRecord.getTnf() == NdefRecord.TNF_WELL_KNOWN && Arrays.equals(ndefRecord.getType(), NdefRecord.RTD_TEXT)) {
                try {
                    return readText(ndefRecord);
                } catch (UnsupportedEncodingException e) {
                }
            }
        }

        return null;
    }

    private String readText(NdefRecord record) throws UnsupportedEncodingException {
        /*
         * See NFC forum specification for "Text Record Type Definition" at 3.2.1
         *
         * http://www.nfc-forum.org/specs/
         *
         * bit_7 defines encoding
         * bit_6 reserved for future use, must be 0
         * bit_5..0 length of IANA language code
         */

        byte[] payload = record.getPayload();

        // Get the Text Encoding
        String textEncoding = ((payload[0] & 128) == 0) ? "UTF-8" : "UTF-16";

        // Get the Language Code
        int languageCodeLength = payload[0] & 0063;

        // String languageCode = new String(payload, 1, languageCodeLength, "US-ASCII");
        // e.g. "en"

        // Get the Text
        return new String(payload, languageCodeLength + 1, payload.length - languageCodeLength - 1, textEncoding);
    }

    public void updateContact(String phoneNo) {
        //create or update file 'emergencyContact.txt'
        String filename = "emergencyContact.txt";

        String fileContents = phoneNo;

        FileOutputStream outputStream;

        //try creating a file 'medicinesFile.txt' with content 'fileContents'
        try {
            outputStream = openFileOutput(filename, Context.MODE_PRIVATE);
            OutputStreamWriter outputWriter = new OutputStreamWriter(outputStream);
            outputWriter.write(fileContents);
            outputWriter.close();

            //Log.e(TAG, "Saved as..." + fileContents); //uncomment to read file if need be

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateNFCID(String nfcID)
    {
        //create or update file 'emergencyContact.txt'
        String filename = "nfcID.txt";

        String fileContents = nfcID;

        FileOutputStream outputStream;

        //try creating a file 'medicinesFile.txt' with content 'fileContents'
        try {
            outputStream = openFileOutput(filename, Context.MODE_PRIVATE);
            OutputStreamWriter outputWriter = new OutputStreamWriter(outputStream);
            outputWriter.write(fileContents);
            outputWriter.close();

            //Log.e(TAG, "Saved as..." + fileContents); //uncomment to read file if need be

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override // Essential to have onPause and onResume methods for the activity.
    protected void onPause() {
        super.onPause();

        stopForegroundDispatch(this, mNfcAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();

        setupForegroundDispatch(this, mNfcAdapter);
    }

}
