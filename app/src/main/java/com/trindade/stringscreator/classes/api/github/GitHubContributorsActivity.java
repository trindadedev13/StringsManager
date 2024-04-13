package com.trindade.stringscreator.classes.api.github;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.trindade.stringscreator.StringsCreatorAppLog;
import com.trindade.stringscreator.databinding.OnlyTestBinding;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.util.List;

public class GitHubContributorsActivity extends AppCompatActivity {

    private static final String BASE_URL = "https://api.github.com/";
    private final StringsCreatorAppLog appLogger = new StringsCreatorAppLog();
    private OnlyTestBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = OnlyTestBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.btn.setText("View users in log");
        binding.btn.setOnClickListener(
                v -> {
                    MaterialAlertDialogBuilder b = new MaterialAlertDialogBuilder(this);
                    b.setTitle("Logs");
                    b.setMessage(appLogger.getLogs());
                    b.setPositiveButton(
                            "OK",
                            (d, w) -> {
                                // Adicione o que deseja fazer quando o botão "OK" é pressionado
                            });
                    b.show();
                });

        try {
            // Configurando Retrofit
            Retrofit retrofit =
                    new Retrofit.Builder()
                            .baseUrl(BASE_URL)
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();

            // Criando uma instância do serviço GitHubService
            GitHubService service = retrofit.create(GitHubService.class);

            // Fazendo a chamada para obter a lista de contribuidores
            Call<List<Contributor>> call =
                    service.getContributors("aquilesTrindade", "StringsCreator");

            // Callback para processar a resposta
            call.enqueue(
                    new Callback<List<Contributor>>() {
                        @Override
                        public void onResponse(
                                Call<List<Contributor>> call,
                                Response<List<Contributor>> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                List<Contributor> contributors = response.body();
                                for (Contributor contributor : contributors) {
                                    String log = "Login: " + contributor.getLogin() +
                                                 ", Avatar URL: " + contributor.getAvatarUrl();
                                    Log.d("Contributor", log);
                                    appLogger.add(log);
                                    
                                    // Obtendo a biografia do usuário
                                    getUserBio(service, contributor.getLogin());
                                }
                            } else {
                                Log.e("GitHub API", "Error: " + response.message());
                            }
                        }

                        @Override
                        public void onFailure(Call<List<Contributor>> call, Throwable t) {
                            Log.e("GitHub API", "Error: " + t.getMessage());
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("GitHub API", "Exception: " + e.getMessage());
        }
    }
    
    private void getUserBio(GitHubService service, String username) {
        Call<User> userCall = service.getUser(username);

        userCall.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    User user = response.body();
                    String bio = user.getBio();
                    appLogger.add("Bio: " + bio);
                } else {
                    Log.e("GitHub API", "Error: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.e("GitHub API", "Error: " + t.getMessage());
            }
        });
    }
}