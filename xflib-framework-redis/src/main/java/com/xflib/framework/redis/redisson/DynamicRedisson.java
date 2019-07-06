package com.xflib.framework.redis.redisson;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.redisson.Redisson;
import org.redisson.api.BatchOptions;
import org.redisson.api.ClusterNodesGroup;
import org.redisson.api.ExecutorOptions;
import org.redisson.api.LocalCachedMapOptions;
import org.redisson.api.MapOptions;
import org.redisson.api.Node;
import org.redisson.api.NodesGroup;
import org.redisson.api.RAtomicDouble;
import org.redisson.api.RAtomicLong;
import org.redisson.api.RBatch;
import org.redisson.api.RBinaryStream;
import org.redisson.api.RBitSet;
import org.redisson.api.RBlockingDeque;
import org.redisson.api.RBlockingQueue;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RBoundedBlockingQueue;
import org.redisson.api.RBucket;
import org.redisson.api.RBuckets;
import org.redisson.api.RCountDownLatch;
import org.redisson.api.RDelayedQueue;
import org.redisson.api.RDeque;
import org.redisson.api.RDoubleAdder;
import org.redisson.api.RGeo;
import org.redisson.api.RHyperLogLog;
import org.redisson.api.RKeys;
import org.redisson.api.RLexSortedSet;
import org.redisson.api.RList;
import org.redisson.api.RListMultimap;
import org.redisson.api.RListMultimapCache;
import org.redisson.api.RLiveObjectService;
import org.redisson.api.RLocalCachedMap;
import org.redisson.api.RLock;
import org.redisson.api.RLongAdder;
import org.redisson.api.RMap;
import org.redisson.api.RMapCache;
import org.redisson.api.RPatternTopic;
import org.redisson.api.RPermitExpirableSemaphore;
import org.redisson.api.RPriorityBlockingDeque;
import org.redisson.api.RPriorityBlockingQueue;
import org.redisson.api.RPriorityDeque;
import org.redisson.api.RPriorityQueue;
import org.redisson.api.RQueue;
import org.redisson.api.RRateLimiter;
import org.redisson.api.RReadWriteLock;
import org.redisson.api.RRemoteService;
import org.redisson.api.RRingBuffer;
import org.redisson.api.RScheduledExecutorService;
import org.redisson.api.RScoredSortedSet;
import org.redisson.api.RScript;
import org.redisson.api.RSemaphore;
import org.redisson.api.RSet;
import org.redisson.api.RSetCache;
import org.redisson.api.RSetMultimap;
import org.redisson.api.RSetMultimapCache;
import org.redisson.api.RSortedSet;
import org.redisson.api.RStream;
import org.redisson.api.RTopic;
import org.redisson.api.RTransaction;
import org.redisson.api.RedissonClient;
import org.redisson.api.TransactionOptions;
import org.redisson.client.codec.Codec;
import org.redisson.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties.Sentinel;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.util.ReflectionUtils;

import com.xflib.framework.redis.jedis.utils.DynamicRedisHolder;
import com.xflib.framework.redis.redisson.configure.DynamicRedissonProperties;
import com.xflib.framework.redis.redisson.configure.RedissonProperties;

public class DynamicRedisson implements RedissonClient {

	private Logger log = LoggerFactory.getLogger(DynamicRedisson.class);
	private Map<String, RedissonClient> redissons = new HashMap<>();

	@Autowired
	private DynamicRedissonProperties dynamicRedissonProperties;

	@Autowired
	private RedissonProperties defaultRedissonProperties;
	
	@Autowired
	private RedisProperties defaultRedisProperties;

	@Autowired
	private ApplicationContext context;

    @PostConstruct
    public void createRedisson() throws UnknownHostException {
        dynamicRedissonProperties.getList().forEach((siteRedissonProperties)->{//创建站点指定redis数据源
          String site=siteRedissonProperties.getSite();
          siteRedissonProperties.getSources().forEach((siteSourceRedissonPrperties)->{
              String source=siteSourceRedissonPrperties.getSource();
              String beanName=String.format("redis-%s-%s", site,source);
              RedisProperties redis=siteSourceRedissonPrperties.getRedis();
              RedissonProperties config=siteSourceRedissonPrperties.getConfig();
              createRedissonClient(beanName, redis, config);
              });
        });
        dynamicRedissonProperties.getSites().forEach((site)->{//创建站点默认redis数据源
            String beanName=String.format("redis-%s-%s", site,"default");
            if (!redissons.containsKey(beanName)){
            	createRedissonClient(beanName, defaultRedisProperties, defaultRedissonProperties);
            }
        });
        String beanName=String.format("redis-%s-%s", "default","default");
        if (!redissons.containsKey(beanName)){// //创建默认站点redis数据源
        	createRedissonClient(beanName, defaultRedisProperties, defaultRedissonProperties);
        }
    }
    
    private void createRedissonClient(String beanName, RedisProperties redisProperties, RedissonProperties config){
        RedissonClient redissonClient=
                (new MetaRedissonConnectionFactory())
                .CreateRedisson(redisProperties, config,context);
        redissons.put(beanName, redissonClient);
        if(log.isDebugEnabled()){
            log.debug("=> Redisson datasource [{}] has registed.",beanName);
        }
    }

    protected RedissonClient determineRedissonClient() {
        RedissonClient redissonClient=null;
        String site=DynamicRedisHolder.getSite();
        site=(site==null||site.isEmpty()?"default":site);
        String source=DynamicRedisHolder.getSource();
        source=(source==null||source.isEmpty()?"default":source);
        String beanName=String.format("redission-%s-%s", site,source);
        if(redissons.containsKey(beanName)){
        	redissonClient= redissons.get(beanName);
            log.debug("=> Current redission datasource is [{}]", beanName);
        }else{
            log.error("=> Redission datasource [{}] is not defined.", beanName);
        }
        return redissonClient;
    }

	@Override
	public <K, V> RStream<K, V> getStream(String name) {
		return determineRedissonClient().getStream(name);
	}

	@Override
	public <K, V> RStream<K, V> getStream(String name, Codec codec) {
		return determineRedissonClient().getStream(name, codec);
	}

	@Override
	public RRateLimiter getRateLimiter(String name) {
		return determineRedissonClient().getRateLimiter(name);
	}

	@Override
	public RBinaryStream getBinaryStream(String name) {
		return determineRedissonClient().getBinaryStream(name);
	}

	@Override
	public <V> RGeo<V> getGeo(String name) {
		return determineRedissonClient().getGeo(name);
	}

	@Override
	public <V> RGeo<V> getGeo(String name, Codec codec) {
		return determineRedissonClient().getGeo(name, codec);
	}

	@Override
	public <V> RSetCache<V> getSetCache(String name) {
		return determineRedissonClient().getSetCache(name);
	}

	@Override
	public <V> RSetCache<V> getSetCache(String name, Codec codec) {
		return determineRedissonClient().getSetCache(name, codec);
	}

	@Override
	public <K, V> RMapCache<K, V> getMapCache(String name, Codec codec) {
		return determineRedissonClient().getMapCache(name, codec);
	}

	@Override
	public <K, V> RMapCache<K, V> getMapCache(String name, Codec codec, MapOptions<K, V> options) {
		return determineRedissonClient().getMapCache(name, codec, options);
	}

	@Override
	public <K, V> RMapCache<K, V> getMapCache(String name) {
		return determineRedissonClient().getMapCache(name);
	}

	@Override
	public <K, V> RMapCache<K, V> getMapCache(String name, MapOptions<K, V> options) {
		return determineRedissonClient().getMapCache(name, options);
	}

	@Override
	public <V> RBucket<V> getBucket(String name) {
		return determineRedissonClient().getBucket(name);
	}

	@Override
	public <V> RBucket<V> getBucket(String name, Codec codec) {
		return determineRedissonClient().getBucket(name, codec);
	}

	@Override
	public RBuckets getBuckets() {
		return determineRedissonClient().getBuckets();
	}

	@Override
	public RBuckets getBuckets(Codec codec) {
		return determineRedissonClient().getBuckets(codec);
	}

	@Override
	public <V> RHyperLogLog<V> getHyperLogLog(String name) {
		return determineRedissonClient().getHyperLogLog(name);
	}

	@Override
	public <V> RHyperLogLog<V> getHyperLogLog(String name, Codec codec) {
		return determineRedissonClient().getHyperLogLog(name, codec);
	}

	@Override
	public <V> RList<V> getList(String name) {
		return determineRedissonClient().getList(name);
	}

	@Override
	public <V> RList<V> getList(String name, Codec codec) {
		return determineRedissonClient().getList(name, codec);
	}

	@Override
	public <K, V> RListMultimap<K, V> getListMultimap(String name) {
		return determineRedissonClient().getListMultimap(name);
	}

	@Override
	public <K, V> RListMultimap<K, V> getListMultimap(String name, Codec codec) {
		return determineRedissonClient().getListMultimap(name, codec);
	}

	@Override
	public <K, V> RListMultimapCache<K, V> getListMultimapCache(String name) {
		return determineRedissonClient().getListMultimapCache(name);
	}

	@Override
	public <K, V> RListMultimapCache<K, V> getListMultimapCache(String name, Codec codec) {
		return determineRedissonClient().getListMultimapCache(name, codec);
	}

	@Override
	public <K, V> RLocalCachedMap<K, V> getLocalCachedMap(String name, LocalCachedMapOptions<K, V> options) {
		return determineRedissonClient().getLocalCachedMap(name, options);
	}

	@Override
	public <K, V> RLocalCachedMap<K, V> getLocalCachedMap(String name, Codec codec,
			LocalCachedMapOptions<K, V> options) {
		return determineRedissonClient().getLocalCachedMap(name, codec, options);
	}

	@Override
	public <K, V> RMap<K, V> getMap(String name) {
		return determineRedissonClient().getMap(name);
	}

	@Override
	public <K, V> RMap<K, V> getMap(String name, MapOptions<K, V> options) {
		return determineRedissonClient().getMap(name, options);
	}

	@Override
	public <K, V> RMap<K, V> getMap(String name, Codec codec) {
		return determineRedissonClient().getMap(name, codec);
	}

	@Override
	public <K, V> RMap<K, V> getMap(String name, Codec codec, MapOptions<K, V> options) {
		return determineRedissonClient().getMap(name, codec, options);
	}

	@Override
	public <K, V> RSetMultimap<K, V> getSetMultimap(String name) {
		return determineRedissonClient().getSetMultimap(name);
	}

	@Override
	public <K, V> RSetMultimap<K, V> getSetMultimap(String name, Codec codec) {
		return determineRedissonClient().getSetMultimap(name, codec);
	}

	@Override
	public <K, V> RSetMultimapCache<K, V> getSetMultimapCache(String name) {
		return determineRedissonClient().getSetMultimapCache(name);
	}

	@Override
	public <K, V> RSetMultimapCache<K, V> getSetMultimapCache(String name, Codec codec) {
		return determineRedissonClient().getSetMultimapCache(name, codec);
	}

	@Override
	public RSemaphore getSemaphore(String name) {
		return determineRedissonClient().getSemaphore(name);
	}

	@Override
	public RPermitExpirableSemaphore getPermitExpirableSemaphore(String name) {
		return determineRedissonClient().getPermitExpirableSemaphore(name);
	}

	@Override
	public RLock getLock(String name) {
		return determineRedissonClient().getLock(name);
	}

	@Override
	public RLock getMultiLock(RLock... locks) {
		return determineRedissonClient().getMultiLock(locks);
	}

	@Override
	public RLock getRedLock(RLock... locks) {
		return determineRedissonClient().getRedLock(locks);
	}

	@Override
	public RLock getFairLock(String name) {
		return determineRedissonClient().getFairLock(name);
	}

	@Override
	public RReadWriteLock getReadWriteLock(String name) {
		return determineRedissonClient().getReadWriteLock(name);
	}

	@Override
	public <V> RSet<V> getSet(String name) {
		return determineRedissonClient().getSet(name);
	}

	@Override
	public <V> RSet<V> getSet(String name, Codec codec) {
		return determineRedissonClient().getSet(name, codec);
	}

	@Override
	public <V> RSortedSet<V> getSortedSet(String name) {
		return determineRedissonClient().getSortedSet(name);
	}

	@Override
	public <V> RSortedSet<V> getSortedSet(String name, Codec codec) {
		return determineRedissonClient().getSortedSet(name, codec);
	}

	@Override
	public <V> RScoredSortedSet<V> getScoredSortedSet(String name) {
		return determineRedissonClient().getScoredSortedSet(name);
	}

	@Override
	public <V> RScoredSortedSet<V> getScoredSortedSet(String name, Codec codec) {
		return determineRedissonClient().getScoredSortedSet(name, codec);
	}

	@Override
	public RLexSortedSet getLexSortedSet(String name) {
		return determineRedissonClient().getLexSortedSet(name);
	}

	@Override
	public RTopic getTopic(String name) {
		return null;
	}

	@Override
	public RTopic getTopic(String name, Codec codec) {
		return determineRedissonClient().getTopic(name, codec);
	}

	@Override
	public RPatternTopic getPatternTopic(String pattern) {
		return determineRedissonClient().getPatternTopic(pattern);
	}

	@Override
	public RPatternTopic getPatternTopic(String pattern, Codec codec) {
		return determineRedissonClient().getPatternTopic(pattern, codec);
	}

	@Override
	public <V> RQueue<V> getQueue(String name) {
		return determineRedissonClient().getQueue(name);
	}

	@Override
	public <V> RDelayedQueue<V> getDelayedQueue(RQueue<V> destinationQueue) {
		return determineRedissonClient().getDelayedQueue(destinationQueue);
	}

	@Override
	public <V> RQueue<V> getQueue(String name, Codec codec) {
		return determineRedissonClient().getQueue(name);
	}

	@Override
	public <V> RRingBuffer<V> getRingBuffer(String name) {
		return determineRedissonClient().getRingBuffer(name);
	}

	@Override
	public <V> RRingBuffer<V> getRingBuffer(String name, Codec codec) {
		return determineRedissonClient().getRingBuffer(name, codec);
	}

	@Override
	public <V> RPriorityQueue<V> getPriorityQueue(String name) {
		return determineRedissonClient().getPriorityQueue(name);
	}

	@Override
	public <V> RPriorityQueue<V> getPriorityQueue(String name, Codec codec) {
		return determineRedissonClient().getPriorityQueue(name, codec);
	}

	@Override
	public <V> RPriorityBlockingQueue<V> getPriorityBlockingQueue(String name) {
		return determineRedissonClient().getPriorityBlockingQueue(name);
	}

	@Override
	public <V> RPriorityBlockingQueue<V> getPriorityBlockingQueue(String name, Codec codec) {
		return determineRedissonClient().getPriorityBlockingQueue(name, codec);
	}

	@Override
	public <V> RPriorityBlockingDeque<V> getPriorityBlockingDeque(String name) {
		return determineRedissonClient().getPriorityBlockingDeque(name);
	}

	@Override
	public <V> RPriorityBlockingDeque<V> getPriorityBlockingDeque(String name, Codec codec) {
		return determineRedissonClient().getPriorityBlockingDeque(name, codec);
	}

	@Override
	public <V> RPriorityDeque<V> getPriorityDeque(String name) {
		return determineRedissonClient().getPriorityBlockingDeque(name);
	}

	@Override
	public <V> RPriorityDeque<V> getPriorityDeque(String name, Codec codec) {
		return determineRedissonClient().getPriorityDeque(name, codec);
	}

	@Override
	public <V> RBlockingQueue<V> getBlockingQueue(String name) {
		return determineRedissonClient().getBlockingQueue(name);
	}

	@Override
	public <V> RBlockingQueue<V> getBlockingQueue(String name, Codec codec) {
		return determineRedissonClient().getBlockingQueue(name, codec);
	}

	@Override
	public <V> RBoundedBlockingQueue<V> getBoundedBlockingQueue(String name) {
		return determineRedissonClient().getBoundedBlockingQueue(name);
	}

	@Override
	public <V> RBoundedBlockingQueue<V> getBoundedBlockingQueue(String name, Codec codec) {
		return determineRedissonClient().getBoundedBlockingQueue(name, codec);
	}

	@Override
	public <V> RDeque<V> getDeque(String name) {
		return determineRedissonClient().getDeque(name);
	}

	@Override
	public <V> RDeque<V> getDeque(String name, Codec codec) {
		return determineRedissonClient().getDeque(name, codec);
	}

	@Override
	public <V> RBlockingDeque<V> getBlockingDeque(String name) {
		return determineRedissonClient().getBlockingDeque(name);
	}

	@Override
	public <V> RBlockingDeque<V> getBlockingDeque(String name, Codec codec) {
		return determineRedissonClient().getBlockingDeque(name, codec);
	}

	@Override
	public RAtomicLong getAtomicLong(String name) {
		return determineRedissonClient().getAtomicLong(name);
	}

	@Override
	public RAtomicDouble getAtomicDouble(String name) {
		return determineRedissonClient().getAtomicDouble(name);
	}

	@Override
	public RLongAdder getLongAdder(String name) {
		return determineRedissonClient().getLongAdder(name);
	}

	@Override
	public RDoubleAdder getDoubleAdder(String name) {
		return determineRedissonClient().getDoubleAdder(name);
	}

	@Override
	public RCountDownLatch getCountDownLatch(String name) {
		return determineRedissonClient().getCountDownLatch(name);
	}

	@Override
	public RBitSet getBitSet(String name) {
		return determineRedissonClient().getBitSet(name);
	}

	@Override
	public <V> RBloomFilter<V> getBloomFilter(String name) {
		return determineRedissonClient().getBloomFilter(name);
	}

	@Override
	public <V> RBloomFilter<V> getBloomFilter(String name, Codec codec) {
		return determineRedissonClient().getBloomFilter(name, codec);
	}

	@Override
	public RScript getScript() {
		return determineRedissonClient().getScript();
	}

	@Override
	public RScript getScript(Codec codec) {
		return determineRedissonClient().getScript(codec);
	}

	@Override
	public RScheduledExecutorService getExecutorService(String name) {
		return determineRedissonClient().getExecutorService(name);
	}

	@Override
	public RScheduledExecutorService getExecutorService(String name, ExecutorOptions options) {
		return determineRedissonClient().getExecutorService(name, options);
	}

	@Override
	public RScheduledExecutorService getExecutorService(Codec codec, String name) {
		return determineRedissonClient().getExecutorService(name, codec);
	}

	@Override
	public RScheduledExecutorService getExecutorService(String name, Codec codec) {
		return determineRedissonClient().getExecutorService(name, codec);
	}

	@Override
	public RScheduledExecutorService getExecutorService(String name, Codec codec, ExecutorOptions options) {
		return determineRedissonClient().getExecutorService(name, codec, options);
	}

	@Override
	public RRemoteService getRemoteService() {
		return determineRedissonClient().getRemoteService();
	}

	@Override
	public RRemoteService getRemoteService(Codec codec) {
		return determineRedissonClient().getRemoteService(codec);
	}

	@Override
	public RRemoteService getRemoteService(String name) {
		return determineRedissonClient().getRemoteService(name);
	}

	@Override
	public RRemoteService getRemoteService(String name, Codec codec) {
		return determineRedissonClient().getRemoteService(name, codec);
	}

	@Override
	public RTransaction createTransaction(TransactionOptions options) {
		return determineRedissonClient().createTransaction(options);
	}

	@Override
	public RBatch createBatch(BatchOptions options) {
		return determineRedissonClient().createBatch(options);
	}

	@Override
	public RBatch createBatch() {
		return determineRedissonClient().createBatch();
	}

	@Override
	public RKeys getKeys() {
		return determineRedissonClient().getKeys();
	}

	@Override
	public RLiveObjectService getLiveObjectService() {
		return determineRedissonClient().getLiveObjectService();
	}

	@Override
	public void shutdown() {
		determineRedissonClient().shutdown();
	}

	@Override
	public void shutdown(long quietPeriod, long timeout, TimeUnit unit) {
		determineRedissonClient().shutdown(quietPeriod, timeout, unit);
	}

	@Override
	public Config getConfig() {
		return determineRedissonClient().getConfig();
	}

	@Override
	public NodesGroup<Node> getNodesGroup() {
		return determineRedissonClient().getNodesGroup();
	}

	@Override
	public ClusterNodesGroup getClusterNodesGroup() {
		return determineRedissonClient().getClusterNodesGroup();
	}

	@Override
	public boolean isShutdown() {
		return determineRedissonClient().isShutdown();
	}

	@Override
	public boolean isShuttingDown() {
		return determineRedissonClient().isShuttingDown();
	}

	private class MetaRedissonConnectionFactory {

//		private RedisProperties redisProperties;
		private RedissonProperties redissonProperties;
		private ApplicationContext ctx;

		public RedissonClient CreateRedisson(RedisProperties redisProperties, RedissonProperties redissonProperties,
				ApplicationContext applicationContext)/* throws IOException*/ {
//			this.redisProperties = redisProperties;
			this.redissonProperties = redissonProperties;
			this.ctx = applicationContext;

			Config config = null;
			Method clusterMethod = ReflectionUtils.findMethod(RedisProperties.class, "getCluster");
			Method timeoutMethod = ReflectionUtils.findMethod(RedisProperties.class, "getTimeout");
			Object timeoutValue = ReflectionUtils.invokeMethod(timeoutMethod, redisProperties);
			int timeout;
			if (null == timeoutValue) {
				timeout = 0;
			} else if (!(timeoutValue instanceof Integer)) {
				Method millisMethod = ReflectionUtils.findMethod(timeoutValue.getClass(), "toMillis");
				timeout = ((Long) ReflectionUtils.invokeMethod(millisMethod, timeoutValue)).intValue();
			} else {
				timeout = (Integer) timeoutValue;
			}

			if (redissonProperties.getConfig() != null) {
				try {
					InputStream is = getConfigStream();
					config = Config.fromJSON(is);
				} catch (IOException e) {
					// trying next format
					try {
						InputStream is = getConfigStream();
						config = Config.fromYAML(is);
					} catch (IOException e1) {
						throw new IllegalArgumentException("Can't parse config", e1);
					}
				}
			} else if (redisProperties.getSentinel() != null) {
				Method nodesMethod = ReflectionUtils.findMethod(Sentinel.class, "getNodes");
				Object nodesValue = ReflectionUtils.invokeMethod(nodesMethod, redisProperties.getSentinel());

				String[] nodes;
				if (nodesValue instanceof String) {
					nodes = convert(Arrays.asList(((String) nodesValue).split(",")));
				} else {
					nodes = convert((List<String>) nodesValue);
				}

				config = new Config();
				config.useSentinelServers().setMasterName(redisProperties.getSentinel().getMaster())
						.addSentinelAddress(nodes).setDatabase(redisProperties.getDatabase()).setConnectTimeout(timeout)
						.setPassword(redisProperties.getPassword());
			} else if (clusterMethod != null && ReflectionUtils.invokeMethod(clusterMethod, redisProperties) != null) {
				Object clusterObject = ReflectionUtils.invokeMethod(clusterMethod, redisProperties);
				Method nodesMethod = ReflectionUtils.findMethod(clusterObject.getClass(), "getNodes");
				List<String> nodesObject = (List) ReflectionUtils.invokeMethod(nodesMethod, clusterObject);

				String[] nodes = convert(nodesObject);

				config = new Config();
				config.useClusterServers().addNodeAddress(nodes).setConnectTimeout(timeout)
						.setPassword(redisProperties.getPassword());
			} else {
				config = new Config();
				String prefix = "redis://";
				Method method = ReflectionUtils.findMethod(RedisProperties.class, "isSsl");
				if (method != null && (Boolean) ReflectionUtils.invokeMethod(method, redisProperties)) {
					prefix = "rediss://";
				}

				config.useSingleServer()
						.setAddress(prefix + redisProperties.getHost() + ":" + redisProperties.getPort())
						.setConnectTimeout(timeout).setDatabase(redisProperties.getDatabase())
						.setPassword(redisProperties.getPassword());
			}

			return Redisson.create(config);
		}

		private String[] convert(List<String> nodesObject) {
			List<String> nodes = new ArrayList<String>(nodesObject.size());
			for (String node : nodesObject) {
				if (!node.startsWith("redis://") && !node.startsWith("rediss://")) {
					nodes.add("redis://" + node);
				} else {
					nodes.add(node);
				}
			}
			return nodes.toArray(new String[nodes.size()]);
		}

		private InputStream getConfigStream() throws IOException {
			Resource resource = ctx.getResource(redissonProperties.getConfig());
			InputStream is = resource.getInputStream();
			return is;
		}
	}
}
