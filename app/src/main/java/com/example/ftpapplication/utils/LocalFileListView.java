package com.example.ftpapplication.utils;

public class LocalFileListView {
        private final String description;
        private final int imgId;
        public LocalFileListView(String description, int imgId) {
            this.description = description;
            this.imgId = imgId;
        }
        public String getDescription() {
            return description;
        }
        public int getImgId() {
            return imgId;
        }
}

