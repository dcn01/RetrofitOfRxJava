package com.laojiang.retrofitofrxjava.downfilesutils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.laojiang.retrofithttp.weight.presenter.downfiles.HttpProgressOnNextListener;
import com.laojiang.retrofitofrxjava.R;
import com.laojiang.retrofitofrxjava.downfilesutils.action.FinalDownFileResult;
import com.laojiang.retrofitofrxjava.downfilesutils.action.OperaDownFileManage;
import com.laojiang.retrofitofrxjava.downfilesutils.downfiles.DownInfo;
import com.laojiang.retrofitofrxjava.downfilesutils.manage.HttpDownManager;

import java.io.File;


/**
 * 类介绍（必填）：最终文件下载封装类
 * Created by Jiang on 2017/3/22 7:57.
 */

public class FinalDownFiles  extends HttpProgressOnNextListener<DownInfo> implements OperaDownFileManage {
    private HttpDownManager manager;
    private Context context;
    private AlertDialog mDownloadDialog;
    private String outUrlStr;
    private String fileUrlStr;
    private DownInfo downInfo;
    private ProgressBar mProgress;
    private FinalDownFileResult fileResult;//返回结果接口
    private TextView tvState;

    public FinalDownFiles(Context context,String fileUrlStr,String outUrlStr,FinalDownFileResult fileResult) {
        this.fileResult = fileResult;
        this.fileUrlStr = fileUrlStr;
        this.outUrlStr = outUrlStr;
        this.context = context;

        // 构造软件下载对话框
        initProgressBar(context);
        //初始化文件下载
        initManage();

    }

    public FinalDownFiles(Context context, String outUrlStr, String fileUrlStr) {
        this.context = context;
        this.outUrlStr = outUrlStr;
        this.fileUrlStr = fileUrlStr;
    }

    private void initManage() {
        manager = HttpDownManager.getInstance();
        //初始化信息
        downInfo = new DownInfo(fileUrlStr);
        File outFile = new File(outUrlStr);
       outFile.mkdirs();
        downInfo.setSavePath(outFile.getAbsolutePath());
        downInfo.setListener(this);
        manager.startDown(downInfo);
    }

    private void initProgressBar(Context context) {
        if (mDownloadDialog!=null){
            mDownloadDialog.dismiss();
            mDownloadDialog=null;
        }
        // 构造软件下载对话框
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("正在下载");
        // 给下载对话框增加进度条
        final LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.dialog_progress_down_file, null);
        mProgress = (ProgressBar) v.findViewById(R.id.progressBar3);
        tvState = (TextView) v.findViewById(R.id.tv_state);
        builder.setView(v);
        // 取消下载
        builder.setNegativeButton(R.string.soft_update_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                manager.stopDown(downInfo);
            }
        });

        mDownloadDialog = builder.create();
        mDownloadDialog.setCanceledOnTouchOutside(false);
        mDownloadDialog.show();
    }

    @Override
    public void onNext(DownInfo baseDownEntity) {
        Toast.makeText(context,baseDownEntity.getSavePath(),Toast.LENGTH_SHORT).show();
        fileResult.onSuccess(baseDownEntity);
    }

    @Override
    public void onStart() {


        fileResult.onStart();
    }

    @Override
    public void onComplete() {
    if (mDownloadDialog!=null){
        mDownloadDialog.dismiss();

    }
        fileResult.onCompleted();
    }

    @Override
    public void onError(Throwable e) {
        super.onError(e);
        fileResult.onErroe(e);
        if (mDownloadDialog!=null){
            mDownloadDialog.dismiss();
        }
    }

    @Override
    public void onPuase() {
        super.onPuase();
        fileResult.onPause();
        tvState.setText("暂停");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.i("停止","...");
        if (mDownloadDialog!=null){
            mDownloadDialog.dismiss();
        }
        fileResult.onStop();
    }

    @Override
    public void updateProgress(long readLength, long countLength) {
        mProgress.setMax((int) countLength);
        mProgress.setProgress((int) readLength);
        Log.i("正在下载==",readLength+"");
    }

    @Override
    public void setPause() {
        manager.pause(downInfo);

    }

    @Override
    public void setStop() {
manager.stopDown(downInfo);

    }

    @Override
    public void stopAll() {
        manager.stopAllDown();
    }

    @Override
    public void deleteDown() {
manager.deleteDown(downInfo);
    }
}