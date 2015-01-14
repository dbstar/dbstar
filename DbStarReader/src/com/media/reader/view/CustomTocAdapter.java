package com.media.reader.view;

import java.util.ArrayList;

import com.media.android.dbstarplayer.ZLTreeAdapter;
import com.media.dbstarplayer.bookmodel.TOCTree;
import com.media.dbstarplayer.dbstarplayer.DbStarPlayerApp;
import com.media.player.common.Utils;
import com.media.reader.vo.TocReference;
import com.media.zlibrary.core.application.ZLApplication;
import com.media.zlibrary.core.resources.ZLResource;
import com.media.zlibrary.core.tree.ZLTree;
import com.media.android.dbstarplayer.R;

import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public final class CustomTocAdapter extends ZLTreeAdapter {
	
	private final String TAG = getClass().getSimpleName();
	public static final int PROCESS_TREE_ITEM_ID = 0;
	public static final int READ_BOOK_ITEM_ID = 1;
	
	private ListView mList = null;
	ArrayList<TocReference> mTocList = new ArrayList<TocReference>();
	ArrayList<TocReference> mChapterList = new ArrayList<TocReference>();
	public CustomTocAdapter(ListView listview,TOCTree root) {
		super(listview, root);
		
		if(root!=null){
			mTocList.add(new TocReference(root.getText(),root.getReference()));
			getTocList(root);
			getChapterList(root);
		}
		for(TocReference reference:mTocList){
			Utils.printLogInfo(TAG, "TocName:"+reference.tocName+", parIndex:"+((reference!=null&&reference.mRef!=null)?reference.mRef.ParagraphIndex:-1));
		}
		
		for(TocReference reference:mChapterList){
			Utils.printLogError(TAG, "Chapter Name:"+reference.tocName+", parIndex:"+((reference!=null&&reference.mRef!=null)?reference.mRef.ParagraphIndex:-1));
		}
		mList = listview;
	}

	private void getTocList(TOCTree root){
		if(root!=null){
			for(TOCTree tree:root.subTrees()){
				mTocList.add(new TocReference(tree.getText(),tree.getReference()));
				getTocList(tree);
			}
		}
	}
	
	private void getChapterList(TOCTree root){
		if(root!=null){
			for(TOCTree tree:root.subTrees()){
				mChapterList.add(new TocReference(tree.getText(),tree.getReference()));
			}
		}
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo menuInfo) {
		final int position = ((AdapterView.AdapterContextMenuInfo)menuInfo).position;
		final TOCTree tree = (TOCTree)getItem(position);
		if (tree.hasChildren()) {
			menu.setHeaderTitle(tree.getText());
			final ZLResource resource = ZLResource.resource("tocView");
			menu.add(0, PROCESS_TREE_ITEM_ID, 0, resource.getResource(isOpen(tree) ? "collapseTree" : "expandTree").getValue());
			menu.add(0, READ_BOOK_ITEM_ID, 0, resource.getResource("readText").getValue());
		}
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final View view = (convertView != null) ? convertView :
			LayoutInflater.from(parent.getContext()).inflate(R.layout.toc_tree_item, parent, false);
		final TOCTree tree = (TOCTree)getItem(position);
		view.setBackgroundColor(tree ==((DbStarPlayerApp)ZLApplication.Instance()).getCurrentTOCElement() ? 0xff808080 : 0);
		setIcon((ImageView)view.findViewById(R.id.toc_tree_item_icon), tree);
		((TextView)view.findViewById(R.id.toc_tree_item_text)).setText(tree.getText());
		return view;
	}

	void openBookText(TOCTree tree) {
		final TOCTree.Reference reference = tree.getReference();
		if (reference != null) {
			mList.setVisibility(View.GONE);
			final DbStarPlayerApp dbstarplayer = (DbStarPlayerApp)ZLApplication.Instance();
//			dbstarplayer.addInvisibleBookmark();
			dbstarplayer.BookTextView.gotoPosition(reference.ParagraphIndex, 0, 0);
			dbstarplayer.showBookTextView();
		}
	}

	@Override
	protected boolean runTreeItem(ZLTree<?> tree) {
		if (super.runTreeItem(tree)) {
			return true;
		}
		openBookText((TOCTree)tree);
		return true;
	}
}