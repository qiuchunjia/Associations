package com.zhiyisoft.associations.util.localVedio;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;

public class VideoProvider {
	private Context context;

	public VideoProvider(Context context) {
		this.context = context;
	}

	public List<LocalVideo> getListVedio() {
		List<LocalVideo> list = null;
		if (context != null) {
			Cursor cursor = context.getContentResolver().query(
					MediaStore.Video.Media.EXTERNAL_CONTENT_URI, null, null,
					null, null);
			if (cursor != null) {
				list = new ArrayList<LocalVideo>();
				while (cursor.moveToNext()) {
					int id = cursor.getInt(cursor
							.getColumnIndexOrThrow(MediaStore.Video.Media._ID));
					String title = cursor
							.getString(cursor
									.getColumnIndexOrThrow(MediaStore.Video.Media.TITLE));
					String album = cursor
							.getString(cursor
									.getColumnIndexOrThrow(MediaStore.Video.Media.ALBUM));
					String artist = cursor
							.getString(cursor
									.getColumnIndexOrThrow(MediaStore.Video.Media.ARTIST));
					String displayName = cursor
							.getString(cursor
									.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME));
					String mimeType = cursor
							.getString(cursor
									.getColumnIndexOrThrow(MediaStore.Video.Media.MIME_TYPE));
					String path = cursor
							.getString(cursor
									.getColumnIndexOrThrow(MediaStore.Video.Media.DATA));
					long duration = cursor
							.getInt(cursor
									.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION));
					long size = cursor
							.getLong(cursor
									.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE));
					LocalVideo video = new LocalVideo(id, title, album, artist,
							displayName, mimeType, path, size, duration);
					list.add(video);
				}
				cursor.close();
			}
		}
		return list;
	}
}