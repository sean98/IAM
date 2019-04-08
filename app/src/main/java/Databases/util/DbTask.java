package Databases.util;
import android.os.AsyncTask;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import Databases.Exceptions.DatabaseException;

public class DbTask<Result> extends AsyncTask<Void,Void, Result> {
    private Callable<Result> doInBackground;
    public Result call() throws Exception {
        return  doInBackground.call();
    }
    private String tag ="DbTask";
    public DbTask(Callable<Result> doInBackground){
        this.doInBackground = doInBackground;
        }
    public DbTask(String tag ,Callable<Result> doInBackground){
        this.doInBackground = doInBackground;
        this.tag+= " , "+tag;
    }
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        Log.i(tag,"executing....");
    }



    @Override
    protected Result doInBackground(Void ... params) {
        try {
            Result result = doInBackground.call();
            Log.i(tag,"success!");
            publishOnSuccess(result);
            return result;
        }
        catch (Exception e) {
            if(e instanceof DatabaseException){
                Log.i(tag,"fail with database exception");
                publishOnFailure((DatabaseException) e);
            }
            else {
                e.printStackTrace();
                Log.e(tag,"Unhandled exception! : "+e.getMessage());
                if(!onUnHandleExceptionListeners.isEmpty())
                    publishOnUnHandleException(e);
                else
                    throw  new RuntimeException(e.getMessage());
            }
        }
        return null;
    }


    @Override
    protected void onPostExecute(Result result) {
        super.onPostExecute(result);
        Log.i(tag,"end!");
        publishOnPostExecute(result);
    }
    private List<OnFailureListener> onFailureListeners = new ArrayList<>();
    private List<OnSuccessListener<Result>> onSuccessListeners = new ArrayList<>();
    private List<OnPostExecuteListener<Result>> onPostExecuteListeners = new ArrayList<>();
    private List<OnUnHandleExceptionListener> onUnHandleExceptionListeners = new ArrayList<>();

    public  DbTask<Result> addOnFailureListener(OnFailureListener listener){
        onFailureListeners.add(listener);
        return this;
    }
    public  DbTask<Result> addOnSuccessListener(OnSuccessListener<Result> listener){
        onSuccessListeners.add(listener);
        return this;
    }
    public  DbTask<Result> addOnUnHandleExceptionListener(OnUnHandleExceptionListener listener){
        onUnHandleExceptionListeners.add(listener);
        return this;
    }

    public  DbTask<Result> addOnPostExecuteListener(OnPostExecuteListener  listener){
        onPostExecuteListeners.add(listener);
        return this;
    }
    public void removeOnFailureListener(OnFailureListener listener){
        onFailureListeners.remove(listener);
    }

    public void removeOnSuccessListener(OnSuccessListener<Result> listener){
        onSuccessListeners.remove(listener);
    }
    public void removeOnPostExecuteListener(OnPostExecuteListener<Result> listener) {
            onPostExecuteListeners.remove(listener);
    }
    public void removeOnUnHandleExceptionListener(OnUnHandleExceptionListener listener) {
        onUnHandleExceptionListeners.remove(listener);
    }


    protected void publishOnFailure(DatabaseException exception){
        for (OnFailureListener f: onFailureListeners) {
            f.onFailure(exception);
        }
    }
    protected void publishOnSuccess(Result result){
        for (OnSuccessListener s: onSuccessListeners) {
            s.onSuccess(result);
        }
    }
    protected void publishOnPostExecute(Result result) {
        for (OnPostExecuteListener s : onPostExecuteListeners) {
            s.onPostExecute(result);
        }
    }
    protected void publishOnUnHandleException(Exception exception) {
        for (OnUnHandleExceptionListener s : onUnHandleExceptionListeners) {
            s.onUnHandleException(exception);
        }
    }

    public interface OnFailureListener{
        void onFailure(DatabaseException exception);
    }
        public interface OnSuccessListener<Result> {
        void onSuccess(Result result);
    }

    public interface OnPostExecuteListener<Result> {
        void onPostExecute(Result result);
    }
    public interface OnUnHandleExceptionListener {
        void onUnHandleException(Exception exception);
    }
}
