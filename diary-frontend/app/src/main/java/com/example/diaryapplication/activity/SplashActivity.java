package com.example.diaryapplication.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.diaryapplication.R;
import com.example.diaryapplication.service.Retrofit.DoRetrofitService;
import com.example.diaryapplication.service.Retrofit.RetrofitClient;
import com.example.diaryapplication.service.Retrofit.SimpleRetrofit;
import com.example.diaryapplication.service.SharedPreferences.PreferenceManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

public class SplashActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        onDeleteActivity(1);
    }

    private void onDeleteActivity(int sec) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // 기본 이미지를 담을 폴더 생성
                createFolder(getFilesDir().getAbsolutePath(),"emoji");

                autoLoginCheckIntentActivity();
            }
        }, 1000 * sec);
    }

    // 자동로그인에 상황 별에 맞는 액티비티 이동
    private void autoLoginCheckIntentActivity() {
        Intent intent;
        // 클라이언트가 로그인을 성공했는지 여부 확인
        boolean isSignInCheck = PreferenceManager.getBoolean(getApplicationContext(), "SIGN_IN_AS");

        if (isAutoLoginStatusCheck() && isSignInCheck) {
            intent = new Intent(getApplicationContext(), MainActivity.class);
        } else {
            intent = new Intent(getApplicationContext(), LoginActivity.class);
        }

        startActivity(intent);
        finish();
    }

    // 자동로그인 상태 확인
    private boolean isAutoLoginStatusCheck() {
        boolean isCheck = PreferenceManager.getBoolean(getApplicationContext(), "AUTO_LOGIN_CHECK");

        if (isCheck) return true;
        else return false;
    }

    // 기본 이모티콘 파일을 담을 폴더 생성
    private void createFolder(String path, String fileName) {
        try {
            File file = new File(path, fileName);

            // 해당 디렉토리가 없을경우 디렉토리를 생성합니다.
            if (!file.exists()) {
                // 폴더 생성
                file.mkdir();
                // 저장 할 이미지 경로를 설정한다
                String emojiPath = path + File.separator + fileName;
                // 서버에서 이미지를 가져와 내부 저장소에 파일 저장
                getBasicEmojiFile(emojiPath);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        // 초반 플래시 화면에서 넘어갈때 뒤로가기 버튼 못누르게 함
        super.onBackPressed();
    }

    private void getBasicEmojiFile(String path) {
        Call<ResponseBody> doEmailAuthCheck = RetrofitClient.getApiService(SplashActivity.this).downloadBasicEmoji();
        SimpleRetrofit.RetrofitService(doEmailAuthCheck, new DoRetrofitService() {
            @Override
            public void doResponse(Call call, Response response) {
                ResponseBody zip = (ResponseBody) response.body();

                // 이모지를 받아서 zip 파일 압축 해제 후 내부저장소에 저장하는 코드 작성
                Log.d("test", "server contacted and has file");

                new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... voids) {
                        boolean writtenToDisk = writeResponseBodyToDisk(zip, path);

                        Log.d("test", "file download was a success? " + writtenToDisk);
                        if (writtenToDisk) {
                            Path source = Paths.get(path + File.separator + "basic.zip");
                            Path target = Paths.get(path + File.separator);

                            unzipFile(source, target);

                            // 파일 삭제
                            try {
                                Files.delete(source);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        return null;
                    }
                }.execute();
            }

            @Override
            public void doFailure(Call call, Throwable t) {
                Toast.makeText(SplashActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void doResponseFailedCodeMatch(int statusCode, String message) {
                Toast.makeText(SplashActivity.this, "server contact failed", Toast.LENGTH_SHORT).show();
            }
        }, SplashActivity.this);
    }

    private boolean writeResponseBodyToDisk(ResponseBody body, String path) {
        try {
            // todo change the file location/name according to your needs
            File futureStudioIconFile = new File(path + File.separator + "basic.zip");

            InputStream inputStream = null;
            OutputStream outputStream = null;

            try {
                byte[] fileReader = new byte[4096];
                long fileSize = body.contentLength();
                long fileSizeDownloaded = 0;
                inputStream = body.byteStream();
                outputStream = new FileOutputStream(futureStudioIconFile);

                while (true) {
                    int read = inputStream.read(fileReader);
                    if (read == -1) {
                        break;
                    }
                    outputStream.write(fileReader, 0, read);
                    fileSizeDownloaded += read;
                    Log.d("test", "file download: " + fileSizeDownloaded + " of " + fileSize);
                }
                outputStream.flush();

                return true;
            } catch (IOException e) {
                return false;
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }
                if (outputStream != null) {
                    outputStream.close();
                }
            }
        } catch (IOException e) {
            return false;
        }
    }

    public void unzipFile(Path sourceZip, Path targetDir) {

        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(sourceZip.toFile()))) {

            // list files in zip
            ZipEntry zipEntry = zis.getNextEntry();
            while (zipEntry != null) {

                boolean isDirectory = false;
                if (zipEntry.getName().endsWith(File.separator)) {
                    isDirectory = true;
                }

                Path newPath = zipSlipProtect(zipEntry, targetDir);
                if (isDirectory) {
                    Files.createDirectories(newPath);
                } else {
                    if (newPath.getParent() != null) {
                        if (Files.notExists(newPath.getParent())) {
                            Files.createDirectories(newPath.getParent());
                        }
                    }
                    // copy files
                    Files.copy(zis, newPath, StandardCopyOption.REPLACE_EXISTING);
                }

                zipEntry = zis.getNextEntry();
            }
            zis.closeEntry();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Path zipSlipProtect(ZipEntry zipEntry, Path targetDir)
            throws IOException {

        // test zip slip vulnerability
        Path targetDirResolved = targetDir.resolve(zipEntry.getName());

        // make sure normalized file still has targetDir as its prefix
        // else throws exception
        Path normalizePath = targetDirResolved.normalize();
        if (!normalizePath.startsWith(targetDir)) {
            throw new IOException("Bad zip entry: " + zipEntry.getName());
        }
        return normalizePath;
    }
}
