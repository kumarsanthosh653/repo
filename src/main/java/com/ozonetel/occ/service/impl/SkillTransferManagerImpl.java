package com.ozonetel.occ.service.impl;

import com.ozonetel.occ.model.Report;
import com.ozonetel.occ.model.StatusMessage;
import com.ozonetel.occ.service.ReportManager;
import com.ozonetel.occ.service.SkillManager;
import com.ozonetel.occ.service.SkillTransferManager;
import java.util.Locale;
import org.apache.log4j.Logger;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;

/**
 *
 * @author pavanj
 */
public class SkillTransferManagerImpl implements SkillTransferManager, MessageSourceAware {

    @Override
    public StatusMessage skillTransfer(String username, String agentId, Long monitorUcid, Long ucid, String did, Long skillId, String skillName) {
        Report report = reportManager.getReportByUcidAndDid(ucid, did);
        report.setTransferType((long) Participant.SKILL.ordinal());
        report.setTransferNow(false);
        report.setBlindTransfer(TransferType.OTHER.ordinal());
        //FIXME remove extra check
        if (skillId != null) {
            report.setTransferSkillId(skillId);
        } else {
            report.setTransferSkillId(skillManager.getSkillsByUserAndSkillName(skillName, username).getId());

        }
        reportManager.save(report);
        return new StatusMessage(Status.SUCCESS, messageSource.getMessage("success.transfer", new Object[]{Participant.SKILL.toReadableString(), Participant.SKILL.toString().toLowerCase(), skillName, skillName}, Locale.getDefault()));
    }

    @Override
    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    public void setReportManager(ReportManager reportManager) {
        this.reportManager = reportManager;
    }

    public void setSkillManager(SkillManager skillManager) {
        this.skillManager = skillManager;
    }

    private ReportManager reportManager;
    private SkillManager skillManager;
    private MessageSource messageSource;
    private static final Logger log = Logger.getLogger(SkillTransferManagerImpl.class);

}
