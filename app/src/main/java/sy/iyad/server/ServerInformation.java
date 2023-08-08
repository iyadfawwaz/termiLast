package sy.iyad.server;


public abstract class ServerInformation {

    public static final String[] SERVER_KEYWORDS={
            "system", "caps-man"    , "console" , "file"    ,"message" ,"tool","monitor",
            "ip"  ,  "log"     ,    "mpls"    ,  "partitions" , "ppp"  ,  "ra","ethernet",
            "certificate" , "disk"  ,   "interface" , "ipv6" ,"beep","quit",
            "metarouter" , "openflow"  ,"port"    ,    "queue" , "ro",
            "ping",
            "accounting",  "arp" ,   "dhcp-client" , "dhcp-server",
            "firewall" , "ipsec"   ,     "neighbor" , "pool"  , "route"  ,
            "settings" , "socks"  ,"tftp"        ,  "upnp",
            "address"    , "cloud" , "dhcp-relay",   "dns",
            "hotspot",   "kid-control",  "packing" ,  "proxy"
            ,"service",  "smb"   ,    "ssh"  ,  "traffic-flow" , "export",

            "debug" , "error",  "find",  "info",  "print" , "warning",

            "aaa" , "active"  ,"l2tp-secret",  "profile" , "secret",
            "host"     ,     "user"   ,    "reset-html",
            "add"  ,    "edit"     , "setup", "disable", "enable",  "remove", "set",
            "cookie" , "ip-binding" , "service-port" , "walled-garden",

            "alert" , "config"  ,"lease" , "network" , "option"  ,"vendor-class-id",

            "backup"   ,"default-configuration" , "history"  , "license" , "ntp"  ,
            "routerboard",  "upgrade" ,  "check-installation" , "reset-configuration"
            , "ssh"    ,     "telnet",
    "clock"  ,  "gps"     ,      "identity" , "logging" , "package" ,
            "scheduler"  ,  "ups"     ,       "serial-terminal"   ,   "ssh-exec",
    "health"       ,          "leds"   ,   "note"    , "resource" , "script"   ,
            "watchdog" , "reboot"   ,      "shutdown"        ,     "sup-output",

            "bandwidth-server",  "graphing"  ,  "romon" ,  "traffic-generator"
            ,"bandwidth-test" , "fetch"   ,    "mac-scan"  ,    "speed-test",  "wol",
            "calea"        ,     "mac-server",  "sms"    ,  "traffic-monitor"
            , "dns-update"   ,   "flood-ping" , "mac-telnet" , "snmp-get",   "torch",
            "e-mail"   ,         "netwatch"   , "sniffer" , "user-manager"
            ,      "ip-scan"  ,   "ping-speed",  "snmp-walk" , "traceroute"
    };

    public static final String USER_PROFILES = "/tool/user-manager/profile/print";
    public static final String CPU_COMMAND = "/system/resource/cpu/print";
    public static final String UPTIME_COMMAND = "/system/resource/print return uptime";
    public static final String VOLTAGE_COMMAND = "/system/health/print where type=V";
    public static final String RUNNING_TRUE = "/interface/ethernet/print where running=true";
    public static final String ETHERNET = "/interface/ethernet/print";
    public static final String ETHERSNAME_COMMAND = "/interface/ethernet/print";
    public static final String DUPLEX = "/interface/ethernet/monitor numbers=";
    public static String IP = "2.2.2.2";
    public static String ADMIN = "admin";
    public static String PASSWORD="995x";
}
