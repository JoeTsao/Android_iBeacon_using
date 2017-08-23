package mustcsie.test.nurseboss;

/**
 * Created by Administrator on 2016/10/29.
 */

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import mustcsie.test.R;
import mustcsie.test.patient.patient_info;

/**
 * Created by Administrator on 2016/10/22.
 */

public class choosepatient extends AppCompatActivity {
    private Button patient_informationBT;
    private Button patient_check_medicineBT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nurseboss_choosepatient_form);
        Intent intent = getIntent();
        final String list = intent.getStringExtra("list");
        final String[] TABID = {"早","中","晚"};
        TextView text1 = (TextView) findViewById(R.id.textView１);
        text1.setText("姓名: "+list);
        patient_informationBT = (Button) findViewById(R.id.nurseboss_information) ;
        patient_check_medicineBT = (Button) findViewById(R.id.nurseboss_lookphy) ;


        patient_informationBT.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(choosepatient.this, patient_info.class);
                intent.putExtra("list2", list);
                choosepatient.this.startActivity(intent);
            }
        });
        patient_check_medicineBT.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dlg=new AlertDialog.Builder(choosepatient.this);
                dlg.setTitle("選擇時段");
                dlg.setItems(TABID, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, final int which) {
                        Intent intent = new Intent(choosepatient.this, lookphy.class);
                        intent.putExtra("list2", list);
                        intent.putExtra("TABID", TABID[which]);
                        choosepatient.this.startActivity(intent);
                    }
                });
                dlg.show();
            }
        });



    }
}
