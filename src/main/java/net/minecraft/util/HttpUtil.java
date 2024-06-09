package net.minecraft.util;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.ServerSocket;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.IProgressUpdate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class HttpUtil {
    public static final ListeningExecutorService executorService = MoreExecutors.listeningDecorator((ExecutorService)Executors.newCachedThreadPool(new ThreadFactoryBuilder().setDaemon(true).setNameFormat("Downloader %d").build()));
    private static final AtomicInteger downloadThreadsStarted = new AtomicInteger(0);
    private static final Logger logger = LogManager.getLogger();

    public static String buildPostString(Map<String, Object> data) {
        StringBuilder stringbuilder = new StringBuilder();
        for (Map.Entry<String, Object> entry : data.entrySet()) {
            if (stringbuilder.length() > 0) {
                stringbuilder.append('&');
            }
            try {
                stringbuilder.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            }
            catch (UnsupportedEncodingException unsupportedencodingexception1) {
                unsupportedencodingexception1.printStackTrace();
            }
            if (entry.getValue() == null) continue;
            stringbuilder.append('=');
            try {
                stringbuilder.append(URLEncoder.encode(entry.getValue().toString(), "UTF-8"));
            }
            catch (UnsupportedEncodingException unsupportedencodingexception) {
                unsupportedencodingexception.printStackTrace();
            }
        }
        return stringbuilder.toString();
    }

    public static String postMap(URL url, Map<String, Object> data, boolean skipLoggingErrors) {
        return HttpUtil.post(url, HttpUtil.buildPostString(data), skipLoggingErrors);
    }

    private static String post(URL url, String content, boolean skipLoggingErrors) {
        try {
            String s2;
            Proxy proxy;
            Proxy proxy2 = proxy = MinecraftServer.getServer() == null ? null : MinecraftServer.getServer().getServerProxy();
            if (proxy == null) {
                proxy = Proxy.NO_PROXY;
            }
            HttpURLConnection httpurlconnection = (HttpURLConnection)url.openConnection(proxy);
            httpurlconnection.setRequestMethod("POST");
            httpurlconnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            httpurlconnection.setRequestProperty("Content-Length", "" + content.getBytes().length);
            httpurlconnection.setRequestProperty("Content-Language", "en-US");
            httpurlconnection.setUseCaches(false);
            httpurlconnection.setDoInput(true);
            httpurlconnection.setDoOutput(true);
            DataOutputStream dataoutputstream = new DataOutputStream(httpurlconnection.getOutputStream());
            dataoutputstream.writeBytes(content);
            dataoutputstream.flush();
            dataoutputstream.close();
            BufferedReader bufferedreader = new BufferedReader(new InputStreamReader(httpurlconnection.getInputStream()));
            StringBuffer stringbuffer = new StringBuffer();
            while ((s2 = bufferedreader.readLine()) != null) {
                stringbuffer.append(s2);
                stringbuffer.append('\r');
            }
            bufferedreader.close();
            return stringbuffer.toString();
        }
        catch (Exception exception) {
            if (!skipLoggingErrors) {
                logger.error("Could not post to " + url, (Throwable)exception);
            }
            return "";
        }
    }

    public static ListenableFuture<Object> downloadResourcePack(final File saveFile, final String packUrl, final Map<String, String> p_180192_2_, final int maxSize, final IProgressUpdate p_180192_4_, final Proxy p_180192_5_) {
        ListenableFuture listenablefuture = executorService.submit(new Runnable(){

            /*
             * Exception decompiling
             */
            @Override
            public void run() {
                /*
                 * This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
                 * 
                 * org.benf.cfr.reader.util.ConfusedCFRException: Tried to end blocks [1[TRYBLOCK]], but top level block is 10[WHILELOOP]
                 *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.processEndingBlocks(Op04StructuredStatement.java:435)
                 *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.buildNestedBlocks(Op04StructuredStatement.java:484)
                 *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op03SimpleStatement.createInitialStructuredBlock(Op03SimpleStatement.java:736)
                 *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisInner(CodeAnalyser.java:850)
                 *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisOrWrapFail(CodeAnalyser.java:278)
                 *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysis(CodeAnalyser.java:201)
                 *     at org.benf.cfr.reader.entities.attributes.AttributeCode.analyse(AttributeCode.java:94)
                 *     at org.benf.cfr.reader.entities.Method.analyse(Method.java:531)
                 *     at org.benf.cfr.reader.entities.ClassFile.analyseMid(ClassFile.java:1055)
                 *     at org.benf.cfr.reader.entities.ClassFile.analyseInnerClassesPass1(ClassFile.java:923)
                 *     at org.benf.cfr.reader.entities.ClassFile.analyseMid(ClassFile.java:1035)
                 *     at org.benf.cfr.reader.entities.ClassFile.analyseTop(ClassFile.java:942)
                 *     at org.benf.cfr.reader.Driver.doJarVersionTypes(Driver.java:245)
                 *     at org.benf.cfr.reader.Driver.doJar(Driver.java:132)
                 *     at org.benf.cfr.reader.CfrDriverImpl.analyse(CfrDriverImpl.java:76)
                 *     at org.benf.cfr.reader.Main.handle(Main.java:69)
                 *     at org.benf.cfr.reader.Main.lambda$new$0(Main.java:32)
                 *     at javax.swing.AbstractButton.fireActionPerformed(AbstractButton.java:2022)
                 *     at javax.swing.AbstractButton$Handler.actionPerformed(AbstractButton.java:2348)
                 *     at javax.swing.DefaultButtonModel.fireActionPerformed(DefaultButtonModel.java:402)
                 *     at javax.swing.DefaultButtonModel.setPressed(DefaultButtonModel.java:259)
                 *     at javax.swing.plaf.basic.BasicButtonListener.mouseReleased(BasicButtonListener.java:252)
                 *     at java.awt.Component.processMouseEvent(Component.java:6539)
                 *     at javax.swing.JComponent.processMouseEvent(JComponent.java:3324)
                 *     at java.awt.Component.processEvent(Component.java:6304)
                 *     at java.awt.Container.processEvent(Container.java:2239)
                 *     at java.awt.Component.dispatchEventImpl(Component.java:4889)
                 *     at java.awt.Container.dispatchEventImpl(Container.java:2297)
                 *     at java.awt.Component.dispatchEvent(Component.java:4711)
                 *     at java.awt.LightweightDispatcher.retargetMouseEvent(Container.java:4904)
                 *     at java.awt.LightweightDispatcher.processMouseEvent(Container.java:4535)
                 *     at java.awt.LightweightDispatcher.dispatchEvent(Container.java:4476)
                 *     at java.awt.Container.dispatchEventImpl(Container.java:2283)
                 *     at java.awt.Window.dispatchEventImpl(Window.java:2746)
                 *     at java.awt.Component.dispatchEvent(Component.java:4711)
                 *     at java.awt.EventQueue.dispatchEventImpl(EventQueue.java:760)
                 *     at java.awt.EventQueue.access$500(EventQueue.java:97)
                 *     at java.awt.EventQueue$3.run(EventQueue.java:709)
                 *     at java.awt.EventQueue$3.run(EventQueue.java:703)
                 *     at java.security.AccessController.doPrivileged(Native Method)
                 *     at java.security.ProtectionDomain$JavaSecurityAccessImpl.doIntersectionPrivilege(ProtectionDomain.java:74)
                 *     at java.security.ProtectionDomain$JavaSecurityAccessImpl.doIntersectionPrivilege(ProtectionDomain.java:84)
                 *     at java.awt.EventQueue$4.run(EventQueue.java:733)
                 *     at java.awt.EventQueue$4.run(EventQueue.java:731)
                 *     at java.security.AccessController.doPrivileged(Native Method)
                 *     at java.security.ProtectionDomain$JavaSecurityAccessImpl.doIntersectionPrivilege(ProtectionDomain.java:74)
                 *     at java.awt.EventQueue.dispatchEvent(EventQueue.java:730)
                 *     at java.awt.EventDispatchThread.pumpOneEventForFilters(EventDispatchThread.java:205)
                 *     at java.awt.EventDispatchThread.pumpEventsForFilter(EventDispatchThread.java:116)
                 *     at java.awt.EventDispatchThread.pumpEventsForHierarchy(EventDispatchThread.java:105)
                 *     at java.awt.EventDispatchThread.pumpEvents(EventDispatchThread.java:101)
                 *     at java.awt.EventDispatchThread.pumpEvents(EventDispatchThread.java:93)
                 *     at java.awt.EventDispatchThread.run(EventDispatchThread.java:82)
                 */
                throw new IllegalStateException("Decompilation failed");
            }
        });
        return listenablefuture;
    }

    public static int getSuitableLanPort() throws IOException {
        ServerSocket serversocket = null;
        int i = -1;
        try {
            serversocket = new ServerSocket(0);
            i = serversocket.getLocalPort();
        }
        finally {
            try {
                if (serversocket != null) {
                    serversocket.close();
                }
            }
            catch (IOException iOException) {}
        }
        return i;
    }

    public static String get(URL url) throws IOException {
        String s2;
        HttpURLConnection httpurlconnection = (HttpURLConnection)url.openConnection();
        httpurlconnection.setRequestMethod("GET");
        BufferedReader bufferedreader = new BufferedReader(new InputStreamReader(httpurlconnection.getInputStream()));
        StringBuilder stringbuilder = new StringBuilder();
        while ((s2 = bufferedreader.readLine()) != null) {
            stringbuilder.append(s2);
            stringbuilder.append('\r');
        }
        bufferedreader.close();
        return stringbuilder.toString();
    }

    static /* synthetic */ Logger access$000() {
        return logger;
    }
}

