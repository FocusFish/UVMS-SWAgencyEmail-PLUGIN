/*
﻿Developed with the contribution of the European Commission - Directorate General for Maritime Affairs and Fisheries
© European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can
redistribute it and/or modify it under the terms of the GNU General Public License as published by the
Free Software Foundation, either version 3 of the License, or any later version. The IFDM Suite is distributed in
the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. You should have received a
copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.
 */
package eu.europa.ec.fisheries.uvms.plugins.sweagencyemail.service;

import eu.europa.ec.fisheries.schema.exchange.common.v1.*;
import eu.europa.ec.fisheries.schema.exchange.plugin.types.v1.EmailType;
import eu.europa.ec.fisheries.schema.exchange.service.v1.SettingListType;
import eu.europa.ec.fisheries.uvms.plugins.sweagencyemail.StartupBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

/**
 **/
@LocalBean
@Stateless
public class PluginService {

    @EJB
    StartupBean startupBean;

    @EJB
    EmailService emailService;

    final static Logger LOG = LoggerFactory.getLogger(PluginService.class);

    /**
     * TODO implement
     *
     * @param report
     * @return
     */
    public AcknowledgeTypeType setReport(ReportType report) {
        return AcknowledgeTypeType.NOK;
    }

    /**
     * TODO implement
     *
     * @param command
     * @return
     */
    public AcknowledgeTypeType setCommand(CommandType command) {
        LOG.info(startupBean.getRegisterClassName() + ".setCommand(" + command.getCommand().name() + ")");
        LOG.debug("timestamp: " + command.getTimestamp());

        EmailType email = command.getEmail();
        if (email != null && CommandTypeType.EMAIL.equals(command.getCommand())) {
            String URL = startupBean.getSetting("endpoint");

            if (email.getFrom() == null || email.getFrom().isEmpty()) {
                email.setFrom(startupBean.getSetting("from"));
            }

            String sendEmail = emailService.sendEmail(email, URL);
            if (sendEmail != null) {
                return AcknowledgeTypeType.OK;
            } else {
                return AcknowledgeTypeType.NOK;
            }
        }

        return AcknowledgeTypeType.NOK;
    }

    /**
     * Set the config values for the swagencyemail
     *
     * @param settings
     * @return
     */
    public AcknowledgeTypeType setConfig(SettingListType settings) {
        LOG.info(startupBean.getRegisterClassName() + ".setConfig()");
        try {
            for (KeyValueType values : settings.getSetting()) {
                LOG.debug("Setting [ " + values.getKey() + " : " + values.getValue() + " ]");
                startupBean.getSettings().put(values.getKey(), values.getValue());
            }
            return AcknowledgeTypeType.OK;
        } catch (Exception e) {
            LOG.error("Failed to set config in {}", startupBean.getRegisterClassName());
            return AcknowledgeTypeType.NOK;
        }

    }

    /**
     * Start the swagencyemail. Use this to enable functionality in the
     * swagencyemail
     *
     * @return
     */
    public AcknowledgeTypeType start() {
        LOG.info(startupBean.getRegisterClassName() + ".start()");
        try {
            startupBean.setIsEnabled(Boolean.TRUE);
            return AcknowledgeTypeType.OK;
        } catch (Exception e) {
            startupBean.setIsEnabled(Boolean.FALSE);
            LOG.error("Failed to start {}", startupBean.getRegisterClassName());
            return AcknowledgeTypeType.NOK;
        }

    }

    /**
     * Stop the swagencyemail. Use this to disable functionality in the
     * swagencyemail
     *
     * @return
     */
    public AcknowledgeTypeType stop() {
        LOG.info(startupBean.getRegisterClassName() + ".stop()");
        try {
            startupBean.setIsEnabled(Boolean.FALSE);
            return AcknowledgeTypeType.OK;
        } catch (Exception e) {
            startupBean.setIsEnabled(Boolean.TRUE);
            LOG.error("Failed to stop {}", startupBean.getRegisterClassName());
            return AcknowledgeTypeType.NOK;
        }
    }

}