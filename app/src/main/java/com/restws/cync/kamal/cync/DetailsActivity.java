package com.restws.cync.kamal.cync;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.Button;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DetailsActivity extends AppCompatActivity {

    @Bind(R.id.lookup_button)
    Button mLookUpButton;

    final String contactNameKey = "contactName_Key";
    final String contactNumberKey = "contactNumber_Key";
    final String contactIpKey = "ContactIP_Key";
    String contactIP;

    TextView mSearchTerm;
    TextView mContactNameView;
    TextView mContactNumberView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.detail_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Bundle extras = getIntent().getExtras();
        String contactName = extras.getString(contactNameKey);
        String contactNumber = extras.getString(contactNumberKey);
        contactIP = extras.getString(contactIpKey);

        mContactNameView = (TextView) findViewById(R.id.detail_item_contact_name);
        mContactNumberView = (TextView) findViewById(R.id.detail_item_contact_number);
        mSearchTerm = (TextView) findViewById(R.id.search_term_input);

        mContactNameView.setText(contactName);
        mContactNumberView.setText(contactNumber);


    }

    @OnClick(R.id.lookup_button)
    public void lookUp() {
        String searchTerm = mSearchTerm.getText().toString();
        getSearchTerm(searchTerm, contactIP);
        mSearchTerm.setText("");
    }

    public void getSearchTerm(String term, String endpoint) {
        ContactLookUpBackgroundTask syncInBackground =
                new ContactLookUpBackgroundTask(getBaseContext(), term, endpoint);
        syncInBackground.execute();

    }

}
