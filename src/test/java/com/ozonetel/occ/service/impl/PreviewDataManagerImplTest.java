package com.ozonetel.occ.service.impl;

import java.util.ArrayList;
import java.util.List;

import com.ozonetel.occ.dao.PreviewDataDao;
import com.ozonetel.occ.model.PreviewData;
import com.ozonetel.occ.service.impl.BaseManagerMockTestCase;

import org.jmock.Expectations;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.*;

public class PreviewDataManagerImplTest extends BaseManagerMockTestCase {
    private PreviewDataManagerImpl manager = null;
    private PreviewDataDao dao = null;

    @Before
    public void setUp() {
        dao = context.mock(PreviewDataDao.class);
        manager = new PreviewDataManagerImpl(dao);
    }

    @After
    public void tearDown() {
        manager = null;
    }

    @Test
    public void testGetPreviewData() {
        log.debug("testing get...");

        final Long preview_data_id = 7L;
        final PreviewData previewData = new PreviewData();

        // set expected behavior on dao
        context.checking(new Expectations() {{
            one(dao).get(with(equal(preview_data_id)));
            will(returnValue(previewData));
        }});

        PreviewData result = manager.get(preview_data_id);
        assertSame(previewData, result);
    }

    @Test
    public void testGetPreviewDatas() {
        log.debug("testing getAll...");

        final List previewDatas = new ArrayList();

        // set expected behavior on dao
        context.checking(new Expectations() {{
            one(dao).getAll();
            will(returnValue(previewDatas));
        }});

        List result = manager.getAll();
        assertSame(previewDatas, result);
    }

    @Test
    public void testSavePreviewData() {
        log.debug("testing save...");

        final PreviewData previewData = new PreviewData();
        // enter all required fields
        
        // set expected behavior on dao
        context.checking(new Expectations() {{
            one(dao).save(with(same(previewData)));
        }});

        manager.save(previewData);
    }

    @Test
    public void testRemovePreviewData() {
        log.debug("testing remove...");

        final Long preview_data_id = -11L;

        // set expected behavior on dao
        context.checking(new Expectations() {{
            one(dao).remove(with(equal(preview_data_id)));
        }});

        manager.remove(preview_data_id);
    }
}