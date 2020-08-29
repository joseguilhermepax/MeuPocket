package br.pro.ednilsonrossi.meupocket.view;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import br.pro.ednilsonrossi.meupocket.R;
import br.pro.ednilsonrossi.meupocket.model.Site;

public class NovoSite extends AppCompatActivity implements View.OnClickListener{

    private EditText tituloEditText;
    private EditText urlEditText;
    private Button salvarButton;
    private Site site;
    private List<Site> siteList = null;

    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_novo_site);

        tituloEditText = findViewById(R.id.edittext_titulo);
        urlEditText = findViewById(R.id.edittext_site);
        salvarButton = findViewById(R.id.button_salvar);
        salvarButton.setOnClickListener(this);

        mSharedPreferences = this.getSharedPreferences(getString(R.string.file_sites), MODE_PRIVATE);
        mEditor = mSharedPreferences.edit();
        recuperaSites();
    }

    @Override
    public void onClick(View view) {

        if(view == salvarButton){
            String titulo;
            String url;

            titulo = tituloEditText.getText().toString();
            url = urlEditText.getText().toString();

            if(titulo.isEmpty() || url.isEmpty()){
                Toast.makeText(this, "Digite o título e a URL", Toast.LENGTH_SHORT).show();
                return;
            }

            site = new Site(titulo, url);
            adicionarBD();
            finish();
        }
    }

    private void recuperaSites(){

        String sites = mSharedPreferences.getString(getString(R.string.key_usuarios_db), "");

        JSONObject jsonObject = new JSONObject();
        JSONArray jsonArray;

        siteList = new ArrayList<>();

        try{

            jsonArray = new JSONArray(sites);

            for(int i=0; i<jsonArray.length(); i++){

                jsonObject = jsonArray.getJSONObject(i);

                site = new Site(jsonObject.getString("titulo"), jsonObject.getString("url"));
                siteList.add(site);
            }
        } catch (JSONException ex){
            siteList = null;
        }

        if (siteList != null) {
            for (Site s : siteList) {
                Log.i("MeuPocket", s.toString());
            }
        }
    }

    private void adicionarBD(){

        JSONObject jsonObject;
        JSONArray jsonArray = new JSONArray();

        if(siteList == null){
            siteList = new ArrayList<>(1);
        }

        siteList.add(site);

        for(Site s : siteList){

            jsonObject = new JSONObject();

            try {
                jsonObject.put("titulo", s.getTitulo());
                jsonObject.put("url", s.getEndereco());
                jsonArray.put(jsonObject);
            }catch (JSONException e){
                Log.e("MeuPocket", "Erro!");
            }
        }

        String sites = jsonArray.toString();

        mEditor.putString(getString(R.string.key_usuarios_db), sites);
        mEditor.commit();

        Log.e("MeuPocket", sites);

    }
}