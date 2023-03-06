package com.ozonetel.occ.dao;

import com.ozonetel.occ.model.PreviewData;


public class PreviewDataDaoTest extends BaseDaoTestCase {
    private PreviewDataDao previewDataDao;

    public void setPreviewDataDao(PreviewDataDao previewDataDao) {
        this.previewDataDao = previewDataDao;
    }

    public void testAddAndRemovePreviewData() throws Exception {
        PreviewData previewData = new PreviewData();

        // enter all required fields

        log.debug("adding previewData...");
        previewData = previewDataDao.save(previewData);

    }
}