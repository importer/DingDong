package com.bigbig.ding.module.notes.main;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bigbig.ding.R;
import com.blankj.utilcode.util.SizeUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.orhanobut.logger.Logger;

import java.util.List;

import com.bigbig.ding.adapter.NoteBottomSheetFolderAdapter;
import com.bigbig.ding.adapter.RvNoteListAdapter;
import com.bigbig.ding.bean.Note;
import com.bigbig.ding.bean.NoteFolder;
import com.bigbig.ding.constants.Constans;
import com.bigbig.ding.constants.NoteListConstans;
import com.bigbig.ding.module.base.BaseActivity;
import com.bigbig.ding.module.lock.modification.LockModificationActivity;
import com.bigbig.ding.module.lock.verification.LockActivity;
import com.bigbig.ding.module.notes.edit.EditNoteActivity;
import com.bigbig.ding.utils.ProgressDialogUtils;
import butterknife.BindView;

public class NoteMainActivity extends BaseActivity<INoteMainView, NoteMainPresenter>
        implements INoteMainView<List<Note>>, View.OnClickListener, View.OnTouchListener {


    @BindView(R.id.rv_note_list)
    RecyclerView mRvNoteList;   // ????????????

    @BindView(R.id.tv_note_list_to_privacy)
    TextView mTvToPrivacy;  // ????????????

    @BindView(R.id.tv_note_list_delete)
    TextView mTvDelete;   // ??????

    @BindView(R.id.tv_note_list_move)
    TextView mTvMove;   // ??????

    @BindView(R.id.fab_note_list_add)
    FloatingActionButton mFabAdd;  // ????????????

    @BindView(R.id.rl_note_list_bottom_bar)
    RelativeLayout mRlBottomBar;   // ???????????????bottomBar

    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawer;

    private MenuItem mSearchMenu;
    private MenuItem mShowModeMenu;
    private MenuItem mCheckAllMenu;

    private float mScrollLastY;
    private float mTouchSlop;

    private RvNoteListAdapter mAdapter = new RvNoteListAdapter();

    @Override
    protected int attachLayoutRes() {
        return R.layout.activity_main;
    }

    @Override
    protected NoteMainPresenter initPresenter() {
        NoteMainPresenter presenter = new NoteMainPresenter();
        presenter.attch(this);
        presenter.setAdapter(mAdapter);
        return presenter;
    }

    @Override
    protected void initViews() {

        mTouchSlop = ViewConfiguration.get(this).getScaledTouchSlop();

        mPresenter.initDataBase();
        initDrawer();
        initAdapter();

        mRvNoteList.setAdapter(mAdapter);
        mRvNoteList.setOnTouchListener(this);

        mPresenter.initNoteRvLayoutManager();
    }

    /**
     * ??????????????????
     */
    public void initDrawer() {
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawer.setDrawerListener(toggle);
        toggle.syncState();
    }

    private void initAdapter() {
        mAdapter.setOnItemChildClickListener(mOnRvClickListener);
        mAdapter.setOnItemChildLongClickListener(mOnRvLongClickListener);
        mAdapter.setEmptyView(getRvEmptyView());
    }

    /**
     * ??????Rv????????????
     *
     * @describe
     */
    BaseQuickAdapter.OnItemChildClickListener mOnRvClickListener = new BaseQuickAdapter.OnItemChildClickListener() {
        @Override
        public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
            mPresenter.onNoteRvClick(position);
        }
    };

    /**
     * ??????Rv????????????
     *
     * @describe
     */
    BaseQuickAdapter.OnItemChildLongClickListener mOnRvLongClickListener = new BaseQuickAdapter.OnItemChildLongClickListener() {
        @Override
        public boolean onItemChildLongClick(BaseQuickAdapter adapter, View view, int position) {
            Logger.d(NoteListConstans.isInSearch);
            if (!NoteListConstans.isInSearch) { // ????????????????????????????????????
                mPresenter.doMultiSelectActionAndChoiceThisItem(position);
            }
            return true;
        }
    };

    private View getRvEmptyView() {
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_empty, null, false);
        return view;
    }

    @Override
    protected void updateViews() {
        mPresenter.start();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab_note_list_add:
                toEditNoteForAdd();
                break;
            case R.id.tv_note_list_delete:
                showDeleteDialog();
                break;
            case R.id.tv_note_list_move:
                mPresenter.moveNotes();
                break;
            case R.id.tv_note_list_to_privacy:
                mPresenter.putNoteToPrivacy();
                break;
        }
    }

    //    ??????dialog
    private void showDeleteDialog() {
        new AlertDialog.Builder(mContext)
                .setTitle("????????????")
                .setMessage("?????????????????????????????????")
                .setPositiveButton("??????",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mPresenter.deleteNotes();
                            }
                        })
                .setNegativeButton("??????", null)
                .show();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        float rawY = event.getRawY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mScrollLastY = rawY;
                break;
            case MotionEvent.ACTION_MOVE:
                if ((rawY - mScrollLastY) > mTouchSlop) {    // ??????????????????
                    setAddFabIn();
                } else if ((mScrollLastY - rawY) > mTouchSlop) {  // ??????????????????
                    setAddFabOut();
                }
                mScrollLastY = rawY;
                break;
        }
        return false;
    }


    @Override
    public void onBackPressed() {
        if (mDrawer.isDrawerOpen(GravityCompat.START)) {//????????????????????????
            mDrawer.closeDrawer(GravityCompat.START);
        } else if (mPresenter.isShowMultiSelectAction()) {//????????????????????????
            mPresenter.cancelMultiSelectAction();
        } else {                                      // ??????
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        initOptionMemu(menu);
        return true;
    }

    /**
     * ?????????toolbar menu
     */
    public void initOptionMemu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);

        mSearchMenu = menu.findItem(R.id.menu_note_search);
        initSearchMenu(mSearchMenu);

        mShowModeMenu = menu.findItem(R.id.menu_note_show_mode);
        mPresenter.initShowModeMenuIcon(mShowModeMenu);

        mCheckAllMenu = menu.findItem(R.id.menu_note_check_all);
    }

    private void initSearchMenu(MenuItem searchItem) {
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        // ??????View?????????????????????
        searchView.setOnQueryTextListener(qreryTextListener);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            MenuItemCompat.setOnActionExpandListener(searchItem,
                    new MenuItemCompat.OnActionExpandListener() {
                        @Override
                        public boolean onMenuItemActionExpand(MenuItem menuItem) {
                            return true;
                        }

                        @Override
                        public boolean onMenuItemActionCollapse(MenuItem menuItem) {
                            mPresenter.cancelFilter();
                            mPresenter.setOutSearch();
                            return true;
                        }
                    });
        } else {
            searchView.setOnCloseListener(new SearchView.OnCloseListener() {
                @Override
                public boolean onClose() {
                    mPresenter.setOutSearch();
                    mPresenter.cancelFilter();
                    return true;
                }
            });
        }
    }

    /**
     * toolbar???SearchView?????????????????????
     */
    private SearchView.OnQueryTextListener qreryTextListener = new SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(String query) {
            return false;
        }

        @Override
        public boolean onQueryTextChange(String newText) {
            mPresenter.setFilter(newText);
            return true;
        }
    };

    /**
     * ??????toolbar???menu
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (mPresenter.isShowMultiSelectAction()) {
            setMenuForMulitiSelectionActionIsShow();
        } else {
            setMenuForMulitiSelectionActionIsNotShow();
        }

        return true;
    }

    private void setMenuForMulitiSelectionActionIsShow() {
        mSearchMenu.setVisible(false);
        mShowModeMenu.setVisible(false);
        mCheckAllMenu.setVisible(true);
        mToolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPresenter.isShowMultiSelectAction()) {
                    mPresenter.cancelMultiSelectAction();
                }
            }
        });
    }

    private void setMenuForMulitiSelectionActionIsNotShow() {
        mSearchMenu.setVisible(true);
        mShowModeMenu.setVisible(true);
        mCheckAllMenu.setVisible(false);
//            ????????????drawer?????????????????????
        MyActionBarDrawerToggle toggle = new MyActionBarDrawerToggle(
                this, mDrawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawer.addDrawerListener(toggle);
        toggle.syncState();
    }


    //    ????????????????????????????????????drawer open???????????????????????????????????????????????????????????????.
    class MyActionBarDrawerToggle extends ActionBarDrawerToggle {
        public MyActionBarDrawerToggle(Activity activity, DrawerLayout drawerLayout, Toolbar toolbar, @StringRes int openDrawerContentDescRes, @StringRes int closeDrawerContentDescRes) {
            super(activity, drawerLayout, toolbar, openDrawerContentDescRes, closeDrawerContentDescRes);
        }

        @Override
        public void onDrawerOpened(View drawerView) {
            super.onDrawerOpened(drawerView);
            if (mPresenter.isShowMultiSelectAction()) {
                mPresenter.cancelMultiSelectAction();
            }
//            ????????????????????????fab???????????????????????????
            setAddFabIn();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.menu_note_show_mode:
                mPresenter.changeNoteRvLayoutManagerAndMenuIcon(item);
                break;
            case R.id.menu_note_check_all:
                mPresenter.doChoiceAllNote();
                break;
            case R.id.menu_note_search:
                mPresenter.setInSearch();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void setRvScrollToFirst() {
        mRvNoteList.scrollToPosition(0);
    }

    @Override
    public void showChoiceNotesCount(String title) {
        setTitle(title);
    }

    @Override
    public void showCurrentNoteFolderName(String title) {
        setTitle(title);
    }

    @Override
    public void updateOptionMenu() {
        // ??????toolbar???????????????????????????onPrepareOptionsMenu???MenuItem item)??????
        supportInvalidateOptionsMenu();
    }

    @Override
    public void setAddFabOut() {
        ObjectAnimator animator = ObjectAnimator.ofFloat(mFabAdd, "translationY", SizeUtils.dp2px(80));
        animator.setDuration(150);
        animator.start();
    }

    @Override
    public void setAddFabIn() {
        ObjectAnimator animator = ObjectAnimator.ofFloat(mFabAdd, "translationY", SizeUtils.dp2px(0));
        animator.setDuration(150);
        animator.start();
    }

    @Override
    public void showAddFab() {
        mFabAdd.setVisibility(View.VISIBLE);
        setAddFabIn();
    }

    @Override
    public void hideAddFab() {
        ObjectAnimator animator = ObjectAnimator.ofFloat(mFabAdd, "translationY", SizeUtils.dp2px(80));
        animator.setDuration(150);
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mFabAdd.setVisibility(View.GONE);
            }
        });
        animator.start();
    }

    @Override
    public void hideDrawer() {
        mDrawer.closeDrawer(GravityCompat.START);
    }

    @Override
    public void hideBottomBar() {
        // ????????????
        ObjectAnimator animator = ObjectAnimator.ofFloat(mRlBottomBar, "translationY", SizeUtils.dp2px(56));
        animator.setDuration(300);
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mRlBottomBar.setVisibility(View.GONE);
            }
        });
        animator.start();
    }

    @Override
    public void showBottomBar() {
        mRlBottomBar.setVisibility(View.VISIBLE);
        //        bottombar???????????????????????????
        ObjectAnimator animator = ObjectAnimator.ofFloat(mRlBottomBar, "translationY", SizeUtils.dp2px(56), 0);
        animator.setDuration(300);
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
            }
        });
        animator.start();
    }

    @Override
    public void setCheckMenuEnable() {
        setButtonEnabled(true, mTvDelete);
        setButtonEnabled(true, mTvMove);
        setButtonEnabled(true, mTvToPrivacy);
    }

    @Override
    public void setCheckMenuUnEnable() {
        setButtonEnabled(false, mTvDelete);
        setButtonEnabled(false, mTvMove);
        setButtonEnabled(false, mTvToPrivacy);
    }

    //    ???????????????????????????
    private void setButtonEnabled(boolean enabled, TextView view) {
        view.setEnabled(enabled);
        if (enabled) {
            view.setTextColor(getResources().getColor(R.color.white));
        } else {
            view.setTextColor(getResources().getColor(R.color.colorWhiteAlpha30));
        }
    }

    @Override
    public void setCheckMenuForAllAndNormal() {
        mTvToPrivacy.setText("????????????");
        mTvDelete.setText("??????");
        mTvMove.setText("??????");

        mTvToPrivacy.setVisibility(View.VISIBLE);
        mTvDelete.setVisibility(View.VISIBLE);
        mTvMove.setVisibility(View.VISIBLE);
    }

    @Override
    public void setCheckMenuForPrivacy() {
        mTvToPrivacy.setText("????????????");
        mTvDelete.setText("??????");

        mTvToPrivacy.setVisibility(View.VISIBLE);
        mTvDelete.setVisibility(View.VISIBLE);
        mTvMove.setVisibility(View.GONE);
    }

    @Override
    public void setCheckMenuForRecycleBin() {
        mTvDelete.setText("??????");
        mTvMove.setText("??????");

        mTvToPrivacy.setVisibility(View.GONE);
        mTvDelete.setVisibility(View.VISIBLE);
        mTvMove.setVisibility(View.VISIBLE);
    }

    @Override
    public void showMoveBottomSheet() {
        final BottomSheetDialog dialog = new BottomSheetDialog(mContext);
//                    ??????contentView
        ViewGroup contentView = (ViewGroup) ((ViewGroup) findViewById(android.R.id.content)).getChildAt(0);
        View root = LayoutInflater.from(mContext).inflate(R.layout.bottom_sheet_folder, contentView, false);
        RecyclerView recyclerView = (RecyclerView) root.findViewById(R.id.recycler_bottom_sheet_folder);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        dialog.setContentView(root);

        recyclerView.setAdapter(getBottomSheetRvAdapter(dialog));

        dialog.show();
    }

    private NoteBottomSheetFolderAdapter getBottomSheetRvAdapter(final BottomSheetDialog dialog) {
        final NoteBottomSheetFolderAdapter folderAdapter = new NoteBottomSheetFolderAdapter();
        folderAdapter.setNewData(mPresenter.getFolderDataList());
        folderAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                NoteFolder folder = folderAdapter.getData().get(position);
                mPresenter.moveNotesToFolder(folder);
                dialog.cancel();
            }
        });
        return folderAdapter;
    }

    @Override
    public void showNoteRecoverDialog(final int position) {
        new AlertDialog.Builder(mContext)
                .setMessage("????????????????????????????????????????????????????????????")
                .setPositiveButton("??????",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mPresenter.recoverNote(position);
                            }
                        })
                .setNegativeButton("??????", null)
                .show();
    }



    @Override
    public void changeNoteRvLayoutManager(RecyclerView.LayoutManager manager) {
        mRvNoteList.setLayoutManager(manager);
        mPresenter.refreshRv();
    }

    @Override
    public void toLockActivity() {
        Intent intent;
        if (Constans.isLocked) {
            intent = new Intent(mContext, LockActivity.class);
        } else {
            intent = new Intent(mContext, LockModificationActivity.class);
        }

        // 5.0??????????????????Activity??????
        if (Build.VERSION.SDK_INT >= 21) {
            Bundle bundle = ActivityOptions.makeSceneTransitionAnimation(this).toBundle();
            startActivityForResult(intent, NoteListConstans.REQUEST_CODE_LOCK, bundle);
        } else {
            startActivityForResult(intent, NoteListConstans.REQUEST_CODE_LOCK);
        }
    }

    @Override
    public void toEditNoteForAdd() {
        Intent intent = new Intent(this, EditNoteActivity.class);
        intent.putExtra("is_add", true);
        startActivityForResult(intent, NoteListConstans.REQUEST_CODE_ADD);
    }

    @Override
    public void toEditNoteForEdit(Note note, int position) {
        Intent intent = new Intent(this, EditNoteActivity.class);
        intent.putExtra("position", position);
        intent.putExtra("is_add", false);
        intent.putExtra("note_id", note.getNoteId());
        intent.putExtra("note_content", note.getNoteContent());
        intent.putExtra("modified_time", note.getModifiedTime());
        startActivityForResult(intent, NoteListConstans.REQUEST_CODE_EDIT);
    }

    private ProgressDialogUtils mProgressDialog=new ProgressDialogUtils(this);

    @Override
    public void showLoading(String message) {
        mProgressDialog.show(message);
    }

    @Override
    public void unShowLoading() {
        mProgressDialog.hide();
    }

    @Override
    public void showSnackbar(String message) {
        Snackbar.make(mFabAdd, message,
                Snackbar.LENGTH_SHORT).setAction("Action", null).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        mPresenter.onActivityResult(requestCode, resultCode, data);
    }
}
