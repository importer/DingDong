package com.bigbig.ding.module.notes.folderList;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.provider.SyncStateContract;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bigbig.ding.R;
import com.bigbig.ding.constants.Constans;
import com.chad.library.adapter.base.BaseQuickAdapter;

import java.util.List;

import com.bigbig.ding.adapter.RvNoteFolderAdapter;
import com.bigbig.ding.bean.NoteFolder;
import com.bigbig.ding.constants.FolderListConstans;
import com.bigbig.ding.constants.NoteListConstans;
import com.bigbig.ding.module.base.BaseFragment;
import com.bigbig.ding.module.notes.folder.FolderActivity;
import com.bigbig.ding.module.notes.main.NoteMainActivity;
import com.bigbig.ding.module.setting.developer.DeveloperActivity;
import com.bigbig.ding.module.setting.feedback.FeedbackActivity;
import com.bigbig.ding.module.setting.main.SettingMainActivity;
import com.bigbig.ding.utils.ThemeUtils;
import com.bigbig.ding.widget.MyDrawable;
import butterknife.BindView;

import static cn.bmob.v3.Bmob.getApplicationContext;

/**
 * <pre>
 *     author : FaDai
 *     e-mail : i_fadai@163.com
 *     time   : 2017/06/19
 *     desc   : xxxx描述
 *     version: 1.0
 * </pre>
 */

public class FolderListFragment extends BaseFragment<IFolderListView, FolderListPresenter>
        implements IFolderListView<List<NoteFolder>> , View.OnClickListener{

    @BindView(R.id.rv_note_list_folder)
    RecyclerView mRvNoteFolder;   // 便签夹列表

    @BindView(R.id.ll_folder_list_setting)
    LinearLayout mLlToSetting;

    private View mHearderView1;

    private View mHearderView2;
    private RelativeLayout mRlAllFolder;
    private ImageView mIvAllIcon;
    private TextView mTvAllTitle;
    private TextView mTvAllCount;

    private View mFoolderView;
    private RelativeLayout mRlPrimaryFolder;
    private ImageView mIvPrimaryIcon;
    private TextView mTvPrimaryTitle;

    private RelativeLayout mRlRecycleFolder;
    private ImageView mIvRecycleIcon;
    private TextView mTvRecycleTitle;

    public FolderListFragment( ){};


    @Override
    protected int attachLayoutRes() {
        return R.layout.fragment_folder;
    }

    @Override
    protected FolderListPresenter initPresenter() {
        FolderListPresenter presenter = new FolderListPresenter();
        presenter.attch(this);
        NoteMainActivity activity=(NoteMainActivity)getActivity();
        presenter.setActivityPresenter(activity.mPresenter);
        return presenter;
    }

    @Override
    protected void initViews() {

        RvNoteFolderAdapter mFolderAdapter = new RvNoteFolderAdapter();
        mFolderAdapter.addHeaderView(getFolderHeaderView());
        mFolderAdapter.addFooterView(getFolderFooterView());
        mFolderAdapter.addHeaderView(getFolderHeaderView2());
        mFolderAdapter.setOnItemClickListener(mNoteItemClickListener);

        mRvNoteFolder.setLayoutManager(new LinearLayoutManager(mContext));
        mRvNoteFolder.setAdapter(mFolderAdapter);

        mPresenter.setAdapter(mFolderAdapter);

        mLlToSetting.setOnClickListener(this);

    }

    /**
     * 便签夹Rv点击事件
     *
     * @describe
     */
    private BaseQuickAdapter.OnItemClickListener mNoteItemClickListener = new BaseQuickAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
            mPresenter.choiceFolder(position,false);
        }
    };

    @Override
    protected void updateViews(boolean isRefresh) {

    }

    /**
     * 获取FolderList的header
     *
     * @describe
     */
    private View getFolderHeaderView() {
        mHearderView1 = LayoutInflater.from(mContext).inflate(R.layout.layout_folder_hearder, null, false);
        TextView tvToCopy=(TextView)mHearderView1.findViewById(R.id.tv_folderlist_to_copy);
        TextView tvToFeedback=(TextView)mHearderView1.findViewById(R.id.tv_folderlist_to_feedback);
        TextView tvToDev=(TextView)mHearderView1.findViewById(R.id.tv_folderlist_to_developer);
        tvToCopy.setOnClickListener(this);
        tvToFeedback.setOnClickListener(this);
        tvToDev.setOnClickListener(this);
        return mHearderView1;
    }
    private View getFolderHeaderView2() {

        mHearderView2= LayoutInflater.from(mContext).inflate(R.layout.layout_folder_hearder_2, null, false);

        mRlAllFolder=(RelativeLayout)mHearderView2.findViewById(R.id.rl_folder_all);
        mIvAllIcon=(ImageView)mHearderView2.findViewById(R.id.iv_folder_all_icon);
        mTvAllTitle=(TextView)mHearderView2.findViewById(R.id.tv_folder_all_title);
        mTvAllCount=(TextView)mHearderView2.findViewById(R.id.tv_folder_all_count);
        TextView tvToEdit=(TextView)mHearderView2.findViewById(R.id.tv_folder_to_edit);

        mRlAllFolder.setOnClickListener(this);
        tvToEdit.setOnClickListener(this);

        return mHearderView2;
    }

    /**
     * 获取FolderList的footer
     *
     * @describe
     */
    private View getFolderFooterView() {

        mFoolderView = LayoutInflater.from(mContext).inflate(R.layout.layout_folder_footer, null, false);

        mRlPrimaryFolder=(RelativeLayout)mFoolderView.findViewById(R.id.rl_folder_privacy);
        mIvPrimaryIcon=(ImageView)mFoolderView.findViewById(R.id.img_folder_privacy_icon);
        mTvPrimaryTitle=(TextView)mFoolderView.findViewById(R.id.tv_folder_privacy_title);

        mRlRecycleFolder=(RelativeLayout)mFoolderView.findViewById(R.id.rl_folder_recycle_bin);
        mIvRecycleIcon=(ImageView)mFoolderView.findViewById(R.id.img_folder_recycle_bin_ic);
        mTvRecycleTitle=(TextView)mFoolderView.findViewById(R.id.tv_folder_recycle_bin_title);

        mRlPrimaryFolder.setOnClickListener(this);
        mRlRecycleFolder.setOnClickListener(this);

        return mFoolderView;
    }

    public void choiceItemAll(){

        mRlAllFolder.setSelected(true);
        mIvAllIcon.setBackground(MyDrawable.getIcFolderSelectedDrawable( ThemeUtils.getColorPrimary(mContext)));
        mTvAllTitle.setTextColor(ThemeUtils.getColorPrimary(mContext));
        mTvAllCount.setTextColor(ThemeUtils.getColorPrimary(mContext));
    }

    public void unChoiceItemAll(){
        mRlAllFolder.setSelected(false);
        mIvAllIcon.setBackgroundResource(R.drawable.ic_folder_un_selected);
        mTvAllTitle.setTextColor(mContext.getResources().getColor(R.color.colorBlackAlpha87));
        mTvAllCount.setTextColor(mContext.getResources().getColor(R.color.colorBlackAlpha54));
    }

    public void choiceItemPrimary(){
        mRlPrimaryFolder.setSelected(true);
        mIvPrimaryIcon.setBackground(MyDrawable.getIcFolderSelectedDrawable(ThemeUtils.getColorPrimary(mContext)));
        mTvPrimaryTitle.setTextColor(ThemeUtils.getColorPrimary(mContext));
    }

    public void unChoiceItemPrimary(){
        mRlPrimaryFolder.setSelected(false);
        mIvPrimaryIcon.setBackgroundResource(R.drawable.ic_folder_un_selected);
        mTvPrimaryTitle.setTextColor(mContext.getResources().getColor(R.color.colorBlackAlpha87));
    }

    public void choiceItemRecycleBin(){
        mRlRecycleFolder.setSelected(true);
        mIvRecycleIcon.setBackground(MyDrawable.getIcFolderSelectedDrawable(ThemeUtils.getColorPrimary(mContext)));
        mTvRecycleTitle.setTextColor(ThemeUtils.getColorPrimary(mContext));
    }

    public void unChoiceItemRecycleBin(){
        mRlRecycleFolder.setSelected(false);
        mIvRecycleIcon.setBackgroundResource(R.drawable.ic_folder_un_selected);
        mTvRecycleTitle.setTextColor(mContext.getResources().getColor(R.color.colorBlackAlpha87));
    }

    @Override
    public void setAllNoteCount(int count) {
        mTvAllCount.setText(count+"");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.rl_folder_all:
                mPresenter.choiceFolder(FolderListConstans.ITEM_ALL,false);
                break;
            case R.id.rl_folder_privacy:
                mPresenter.choiceFolder(FolderListConstans.ITEM_PRIMARY,false);
                break;
            case R.id.rl_folder_recycle_bin:
                mPresenter.choiceFolder(FolderListConstans.ITEM_RECYCLE,false);
                break;
            case R.id.ll_folder_list_setting:
                toSettingActivity();
                break;
            case R.id.tv_folder_to_edit:
                toEditFolderActivity();
                break;
            case R.id.tv_folderlist_to_developer:
                toAboutDeveloper();
                break;
            case R.id.tv_folderlist_to_feedback:
                toFeedbackActivity();
                break;
            case R.id.tv_folderlist_to_copy:
                toCopyRegid();
//                Constans.mRegId
                break;
        }
    }

    private void toSettingActivity(){
        Intent intent=new Intent(getActivity(), SettingMainActivity.class);
        startActivity(intent);
    }

    private void toEditFolderActivity(){
        Intent intent=new Intent(getActivity(), FolderActivity.class);
        intent.putExtra("current_folder_id",mPresenter.getCurrentFolderId());
        getActivity().startActivityForResult(intent, NoteListConstans.REQUEST_CODE_EDIT_FOLDER);
    }

    private void toAboutDeveloper() {
       Intent intent=new Intent(getActivity(), DeveloperActivity.class);
        getActivity().startActivity(intent);
    }

    private void toFeedbackActivity(){
        Intent intent=new Intent(getActivity(), FeedbackActivity.class);
        getActivity().startActivity(intent);
    }
    /**
     * 复制内容到剪贴板
     *
     * @param content
     * @param context
     */
    public void copyContentToClipboard(String content, Context context) {
        //获取剪贴板管理器：
        ClipboardManager cm = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        // 创建普通字符型ClipData
        ClipData mClipData = ClipData.newPlainText("Label", content);
        // 将ClipData内容放到系统剪贴板里。
        cm.setPrimaryClip(mClipData);
    }

    private void toCopyRegid(){
        final String showText;
        if (Constans.mRegId == null) {
            showText="唯一值获取失败，请重新获取！";
        } else {
            //获取前台显示网址的控件
            showText="https://service-mm0wgphf-1258509752.cd.apigw.tencentcs.com/release/APIService-mipushServer/dd/sendmsg?id=" + Constans.xmRegId + "&title=标题(可选值)&msg=测试提交数据";

        }
        copyContentToClipboard(showText,getApplicationContext());
        Toast.makeText(getApplicationContext(), R.string.copy_to_clip, Toast.LENGTH_LONG).show();
    }

}
