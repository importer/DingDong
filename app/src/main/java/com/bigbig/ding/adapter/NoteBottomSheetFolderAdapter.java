package com.bigbig.ding.adapter;

import com.bigbig.ding.R;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import com.bigbig.ding.bean.NoteFolder;

/**
 * <pre>
 *     author : FaDai
 *     e-mail : i_fadai@163.com
 *     time   : 2017/06/27
 *     desc   : xxxx描述
 *     version: 1.0
 * </pre>
 */

public class NoteBottomSheetFolderAdapter extends BaseQuickAdapter<NoteFolder, BaseViewHolder> {

    public NoteBottomSheetFolderAdapter() {
        super(R.layout.item_note_bottom_folder);
    }

    @Override
    protected void convert(BaseViewHolder helper, NoteFolder item) {
        helper.setText(R.id.tv_folder_title_bottom_sheet,item.getFolderName());
    }


}
