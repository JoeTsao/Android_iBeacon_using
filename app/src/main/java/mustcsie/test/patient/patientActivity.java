package mustcsie.test.patient;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import mustcsie.test.R;

/**
 * Created by Administrator on 2016/10/22.
 */

public class patientActivity extends AppCompatActivity {
    private Button patient_informationBT;

    private Button patient_physiologicalBT;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.patient);
        Intent intent = getIntent();
        final String list = intent.getStringExtra("list");
        patient_informationBT = (Button) findViewById(R.id.patient_information) ;
        patient_physiologicalBT = (Button) findViewById(R.id.patient_physiological) ;

        patient_informationBT.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(patientActivity.this, patient_info.class);
                intent.putExtra("list2", list);
                patientActivity.this.startActivity(intent);
            }
        });

        patient_physiologicalBT.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(patientActivity.this, patient_physiologicalActivity.class);
                intent.putExtra("list2", list);
                patientActivity.this.startActivity(intent);
            }
        });


    }
}
