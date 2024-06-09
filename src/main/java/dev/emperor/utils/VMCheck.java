package dev.emperor.utils;

import dev.emperor.Client;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class VMCheck {
    private static volatile VMCheck INSTANCE;
    private String[] PROCESS_NAMES;
    private String[] DEBUG_ARGS;
    private String[] MAC_ADDRESSES;
    private final byte[] ANYRUN_X64 = new byte[]{83, 72, -125, -20, 32, -24, 70, -22, -1, -1, 72, -117, 29, -85, 85, 0, 0, -71, -12, 17, 0, 0, -1, -45, -21, -9};
    private final byte[] ANYRUN_X86 = new byte[]{-115, 76, 36, 4, -125, -28, -16, -1, 113, -4, 85, -119, -27, 81, -125, -20, 20, -24, -38, -4, -1, -1, -115, 118, 0, -115, -68, 39, 0, 0, 0, 0, -57, 4, 36, -12, 1, 0, 0, -24, -124, -1, -1, -1, -125, -20, 4, -21, -17};
    private String[] FILE_NAMES;

    private VMCheck() {
        this.init();
        if (INSTANCE != null) {
            throw new RuntimeException("INSTANTIATION ERROR");
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public static VMCheck getInstance() {
        if (INSTANCE != null) return INSTANCE;
        Class<Client> clazz = Client.class;
        synchronized (Client.class) {
            if (INSTANCE != null) return INSTANCE;
            INSTANCE = new VMCheck();
            // ** MonitorExit[var0] (shouldn't be in output)
            return INSTANCE;
        }
    }

    private native void init();

    public boolean runChecks() {
        return this.checkAnyRun() || this.checkMac() || this.checkFiles() || this.checkVMFiles() || this.checkVirtualBoxFiles() || this.checkUsername() || this.checkProcess();
    }

    private boolean checkMac() {
        try {
            for (String mac : this.MAC_ADDRESSES) {
                if (!this.getMacAddress().equalsIgnoreCase(mac)) continue;
                return true;
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
        return false;
    }

    private List<String> listRunningProcesses() {
        ArrayList<String> processes = new ArrayList<String>();
        try {
            String line;
            Process p = Runtime.getRuntime().exec("tasklist.exe /fo csv /nh");
            BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
            while ((line = input.readLine()) != null) {
                if (line.trim().equals("")) continue;
                line = line.substring(1);
                processes.add(line.substring(0, line.indexOf("\"")));
            }
            input.close();
        }
        catch (Exception exception) {
            // empty catch block
        }
        return processes;
    }

    private boolean checkProcess() {
        for (String runningProcess : this.listRunningProcesses()) {
            for (String process : this.PROCESS_NAMES) {
                if (!runningProcess.toLowerCase().contains(process)) continue;
                return true;
            }
        }
        return false;
    }

    private boolean checkFiles() {
        for (String file : this.FILE_NAMES) {
            if (!Files.exists(Paths.get(file, new String[0]), new LinkOption[0])) continue;
            return true;
        }
        return false;
    }

    private String getMacAddress() throws UnknownHostException {
        StringBuilder macAddressBuilder = new StringBuilder();
        try {
            InetAddress ipAddress = InetAddress.getLocalHost();
            NetworkInterface networkInterface = NetworkInterface.getByInetAddress(ipAddress);
            byte[] macAddressBytes = networkInterface.getHardwareAddress();
            for (int macAddressByteIndex = 0; macAddressByteIndex < macAddressBytes.length; ++macAddressByteIndex) {
                String macAddressHexByte = String.format("%02X", macAddressBytes[macAddressByteIndex]);
                macAddressBuilder.append(macAddressHexByte);
                if (macAddressByteIndex == macAddressBytes.length - 1) continue;
                macAddressBuilder.append(":");
            }
        }
        catch (SocketException | UnknownHostException iOException) {
            // empty catch block
        }
        return macAddressBuilder.toString();
    }

    private boolean checkVMFiles() {
        String osNameMatch = System.getProperty("os.name").toLowerCase();
        if (osNameMatch.contains("linux")) {
            return new File("/etc/vmware-tools").exists();
        }
        if (osNameMatch.contains("windows")) {
            String path = !System.getProperty("os.arch").equalsIgnoreCase("x86") ? System.getenv("ProgramFiles(X86)") : System.getenv("ProgramFiles");
            return new File(path + "\\VMware\\VMware Tools").exists();
        }
        if (osNameMatch.contains("mac os") || osNameMatch.contains("macos") || osNameMatch.contains("darwin")) {
            return new File("/Library/Application Support/VMware Tools").exists();
        }
        return false;
    }

    private boolean checkVirtualBoxFiles() {
        String osNameMatch = System.getProperty("os.name").toLowerCase();
        if (osNameMatch.contains("linux")) {
            return new File("/etc/init.d/vboxadd").exists();
        }
        if (osNameMatch.contains("windows")) {
            String path = !System.getProperty("os.arch").equalsIgnoreCase("x86") ? System.getenv("ProgramFiles(X86)") : System.getenv("ProgramFiles");
            return new File(path + "\\Oracle\\VirtualBox Guest Additions").exists();
        }
        return false;
    }

    private boolean checkUsername() {
        String username = System.getProperty("user.name");
        return username.equals("WDAGUtilityAccount") || username.toLowerCase().startsWith("hal-");
    }

    private boolean checkAnyRun() {
        try {
            File f = new File(FileUtil.SYS_DIR, "windanr.exe");
            if (!f.exists()) {
                return false;
            }
            return this.searchSig(f, System.getenv("ProgramFiles(x86)") != null ? this.ANYRUN_X64 : this.ANYRUN_X86);
        }
        catch (Exception e) {
            return false;
        }
    }

    private boolean searchSig(File file, byte[] badBytes) throws Exception {
        if (file.exists()) {
            if (file.isDirectory()) {
                if (file.canRead()) {
                    try {
                        for (File subFiles : Objects.requireNonNull(file.listFiles())) {
                            this.searchSig(subFiles, badBytes);
                        }
                    }
                    catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                }
            } else if (file.isFile() && file.canRead()) {
                byte[] fileBytes = Files.readAllBytes(file.toPath());
                for (int i = 0; i <= fileBytes.length - badBytes.length; ++i) {
                    if (!this.match(fileBytes, badBytes, i)) continue;
                    return true;
                }
            }
        }
        return false;
    }

    private boolean match(byte[] haystack, byte[] needle, int start) {
        if (needle.length + start <= haystack.length) {
            for (int i = 0; i < needle.length; ++i) {
                if (needle[i] != haystack[i + start]) continue;
                return true;
            }
        }
        return false;
    }

    private boolean checkArguments() {
        for (String arg : ManagementFactory.getRuntimeMXBean().getInputArguments()) {
            for (String blacklist : this.DEBUG_ARGS) {
                if (!arg.toLowerCase().startsWith(blacklist)) continue;
                return true;
            }
        }
        return false;
    }
}

