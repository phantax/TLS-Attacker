/**
 * TLS-Attacker - A Modular Penetration Testing Framework for TLS
 *
 * Copyright 2014-2016 Ruhr University Bochum / Hackmanit GmbH
 *
 * Licensed under Apache License 2.0
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package de.rub.nds.tlsattacker.tlsserver;

import com.beust.jcommander.JCommander;
import de.rub.nds.tlsattacker.tls.config.delegate.GeneralDelegate;
import de.rub.nds.tlsattacker.tls.exceptions.ConfigurationException;
import de.rub.nds.tlsattacker.tls.exceptions.WorkflowExecutionException;
import de.rub.nds.tlsattacker.tls.util.LogLevel;
import de.rub.nds.tlsattacker.tls.util.WorkflowTraceSerializer;
import de.rub.nds.tlsattacker.tls.workflow.TlsConfig;
import de.rub.nds.tlsattacker.tls.workflow.TlsContext;
import de.rub.nds.tlsattacker.tls.workflow.WorkflowExecutor;
import de.rub.nds.tlsattacker.tls.workflow.WorkflowExecutorFactory;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import javax.xml.bind.JAXBException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author Robert Merget - robert.merget@rub.de
 */
public class TlsServer {

    private static final Logger LOGGER = LogManager.getLogger(TlsServer.class);

    public static void main(String args[]) {
        ServerCommandConfig config = new ServerCommandConfig(new GeneralDelegate());
        JCommander commander = new JCommander(config);
        Exception ex = null;
        try {
            commander.parse(args);
        } catch (Exception E) {
            LOGGER.info("Could not parse provided parameters");
            commander.usage();
            ex = E;
        }
        if (ex == null) {
            // Cmd was parsable
            TlsConfig tlsConfig = null;
            try {
                tlsConfig = config.createConfig();
                TlsServer server = new TlsServer();
                LOGGER.info("TLS-Server starting on port " + tlsConfig.getServerPort());
                server.startTlsServer(tlsConfig);
            } catch (ConfigurationException E) {
                LOGGER.info("Could not initialize Configuration", E);
            }

        }
    }

    public void startTlsServer(TlsConfig config) {
        TlsContext tlsContext = new TlsContext(config);
        WorkflowExecutor workflowExecutor = WorkflowExecutorFactory.createWorkflowExecutor(config.getExecutorType(),
                tlsContext);

        try {
            workflowExecutor.executeWorkflow();
        } catch (WorkflowExecutionException ex) {
            LOGGER.log(LogLevel.CONSOLE_OUTPUT,
                    "The TLS protocol flow was not executed completely, follow the debug messages for more information.");
            LOGGER.debug(ex.getLocalizedMessage(), ex);
        }

        if (config.getWorkflowOutput() != null && !config.getWorkflowOutput().isEmpty()) {
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(config.getWorkflowOutput());
                WorkflowTraceSerializer.write(fos, tlsContext.getWorkflowTrace());
            } catch (FileNotFoundException ex) {
                LOGGER.info("Could not serialize WorkflowTrace.", ex);
            } catch (JAXBException | IOException ex) {
                LOGGER.info("Could not serialize WorkflowTrace.", ex);
            } finally {
                try {
                    fos.close();
                } catch (IOException ex) {
                    LOGGER.info("Could not serialize WorkflowTrace.", ex);
                }
            }
        }
    }
}
