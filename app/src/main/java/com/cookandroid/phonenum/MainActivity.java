package com.cookandroid.phonenum;

import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;


import android.content.pm.PackageManager;
import android.os.Build;
import android.Manifest;

public class MainActivity extends AppCompatActivity {

    private static final int PERMISSIONS_REQUEST_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 런타임 권한 요청
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED
                    || checkSelfPermission(Manifest.permission.WRITE_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{
                        Manifest.permission.READ_CONTACTS,
                        Manifest.permission.WRITE_CONTACTS
                }, PERMISSIONS_REQUEST_CODE);
            } else {
                setupSyncButton();
            }
        } else {
            setupSyncButton();
        }
    }

    private void setupSyncButton() {
        Button syncButton = findViewById(R.id.syncButton);
        syncButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ContactSyncTask(MainActivity.this, "http://0.0.0.0/phone/contacts_api.php").execute();
            }
        });

        Button syncButton2 = findViewById(R.id.syncButton2);
        syncButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ContactSyncTask(MainActivity.this, "http://0.0.0.0/phone/contacts_api2.php").execute();
            }
        });
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                setupSyncButton();
            } else {
                Toast.makeText(this, "권한이 필요합니다.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // 연락처 동기화 AsyncTask
    private static class ContactSyncTask extends AsyncTask<Void, Void, JSONArray> {
        private Context context;
        private String apiUrl;

        public ContactSyncTask(Context context, String apiUrl) {
            this.context = context;
            this.apiUrl = apiUrl;
        }

        @Override
        protected JSONArray doInBackground(Void... voids) {
            try {
                URL url = new URL(apiUrl);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");

                BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                StringBuilder stringBuilder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line);
                }

                String response = stringBuilder.toString();
                Log.d("API Response", "Response: " + response);

                return new JSONArray(response);
            } catch (Exception e) {
                Log.e("JSON Parsing Error", "Error parsing JSON: " + e.getMessage());
                e.printStackTrace();
            }
            return null;
        }


        @Override
        protected void onPostExecute(JSONArray contacts) {
            super.onPostExecute(contacts);

            if (contacts != null && contacts.length() > 0) {
                // JSON 데이터를 로깅하여 확인
                Log.d("ContactsSync", "Contacts: " + contacts.toString());  // 서버에서 받은 JSON 데이터 확인

                // 연락처를 주소록에 추가하는 메소드 호출
                addContactsToPhone(contacts);
            } else {
                Log.e("ContactsSync", "No contacts found.");
                Toast.makeText(context, "서버에서 연락처를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show();
            }
        }

        private boolean isPhoneNumberExists(ContentResolver contentResolver, String phoneNumber) {
            String[] projection = new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER};
            String selection = ContactsContract.CommonDataKinds.Phone.NUMBER + " = ?";
            String[] selectionArgs = new String[]{phoneNumber};

            try (Cursor cursor = contentResolver.query(
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    projection,
                    selection,
                    selectionArgs,
                    null)) {

                return cursor != null && cursor.getCount() > 0; // 결과가 있으면 이미 존재하는 번호
            }
        }

        private void addContactsToPhone(JSONArray contacts) {
            ContentResolver contentResolver = context.getContentResolver();
            ArrayList<ContentProviderOperation> operations = new ArrayList<>();

            try {
                for (int i = 0; i < contacts.length(); i++) {
                    JSONObject contact = contacts.getJSONObject(i);
                    String name = contact.getString("nam");
                    String phone = contact.getString("phone");

                    // 휴대폰 주소록에서 해당 전화번호가 존재하는지 확인
                    if (isPhoneNumberExists(contentResolver, phone)) {
                        Log.d("ContactsSync", "이미 존재하는 연락처: " + phone);
                        continue;  // 중복되면 추가하지 않음
                    }

                    Log.d("ContactsSync", "추가할 연락처: " + name + " - " + phone);

                    int rawContactInsertIndex = operations.size();
                    ContentProviderOperation.Builder builder = ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI);
                    builder.withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null);
                    builder.withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null);
                    operations.add(builder.build());

                    // 연락처 이름 추가.
                    builder = ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI);
                    builder.withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, rawContactInsertIndex);
                    builder.withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE);
                    builder.withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, name);
                    operations.add(builder.build());

                    // 전화번호 추가
                    builder = ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI);
                    builder.withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, rawContactInsertIndex);
                    builder.withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
                    builder.withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, phone);
                    builder.withValue(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE);
                    operations.add(builder.build());
                }

                // 변경 사항을 적용
                if (!operations.isEmpty()) {
                    contentResolver.applyBatch(ContactsContract.AUTHORITY, operations);
                    Log.d("ContactsSync", "연락처 동기화 완료!");
                    Toast.makeText(context, "연락처 저장 완료", Toast.LENGTH_SHORT).show();
                } else {
                    Log.d("ContactsSync", "추가할 새로운 연락처가 없음");
                    Toast.makeText(context, "이미 모든 연락처가 존재합니다.", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


    }
}
