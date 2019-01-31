可设置的参数
    private String host = "localhost";
    private int port = 5672;
    private String username;
    private String password;
    private String virtualHost;
    private String addresses;
    private Integer requestedHeartbeat;
    private boolean publisherConfirms;
    private boolean publisherReturns;
    private Integer connectionTimeout;
    private final Ssl ssl = new Ssl();
    private final Cache cache = new Cache();
    private List<Address> parsedAddresses;
    
内置参数(不可设置)
    private final Listener listener = new Listener();
    private final Template template = new Template();
    