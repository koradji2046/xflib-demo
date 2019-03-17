/**Copyright: Copyright (c) 2016, 湖南强智科技发展有限公司*/
package com.xflib.framework.database;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Map;
import java.util.Properties;

import org.hibernate.LockMode;
import org.hibernate.LockOptions;
import org.hibernate.MappingException;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;
import org.hibernate.engine.jdbc.internal.FormatStyle;
import org.hibernate.engine.jdbc.spi.JdbcServices;
import org.hibernate.engine.jdbc.spi.SqlStatementLogger;
import org.hibernate.engine.spi.SessionEventListenerManager;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.id.IdentifierGeneratorHelper;
import org.hibernate.id.IdentifierGeneratorHelper.BasicHolder;
import org.hibernate.id.IntegralDataTypeHolder;
import org.hibernate.id.enhanced.AccessCallback;
import org.hibernate.id.enhanced.TableGenerator;
import org.hibernate.internal.CoreMessageLogger;
import org.hibernate.internal.util.StringHelper;
import org.hibernate.internal.util.config.ConfigurationHelper;
import org.hibernate.jdbc.AbstractReturningWork;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.type.Type;
import org.jboss.logging.Logger;

import com.xflib.framework.common.BaseException;
import com.xflib.framework.configuration.SysProperties;
import com.xflib.framework.utils.DateTimeUtils;
import com.xflib.framework.utils.JedisUtil;
import com.xflib.framework.utils.StringUtils;

/**
 *  IdGenerator数据库唯一ID生成器
 *
 * History:<br> 
 *    . 1.0.0.20161015, com.qzdatasoft.koradji, Create<br>
 *
 */

//@GeneratedValue(generator = "idGenerator")  
//@GenericGenerator(name = "idGenerator", strategy = "com.qzdatasoft.framework.database.IdGenerator",
//parameters = {
//		@Parameter(name = "table_name", value = "t_id_generator") ,//用于生成流水号的表
//		@Parameter(name = "segment_column_name", value = "gen_name") ,//流水号名称字段
//		@Parameter(name = "segment_value", value = "tResource") ,//流水号名称
//		@Parameter(name = "value_column_name", value = "gen_value") ,流水号值字段
//		@Parameter(name = "initial_value", value = "1") ,//流水号初始值
//		@Parameter(name = "segment_value_length", value = "6") ,//流水号值长度(前面补0)
//		@Parameter(name = "sequenceValueRecycle", value = "DAY") ,//流水号值循环规则
//		@Parameter(name = "lastTime_column_name", value = "last_time") ,//最后修改时间
//		@Parameter(name = "increment_size", value ="1")//流水号增长的步长
//	}
//)
public class IdGenerator extends TableGenerator {
	/**
	 * 
	 * 流水号归零周期
	 *
	 * History:<br>
	 * . 1.0.0.20161018, com.qzdatasoft.koradji, Create<br>
	 *
	 */
	public enum RecycleType {
		/** 按天 */		DAY,
		/** 按周 */		WEEK,
		/** 按月 */		MONTH,
		/** 按季 */		QUARTER,
		/** 按半年 */		HALFOONEYEAR,
		/** 按年 */		YEAR,
		/** 直到最大 */		MAX,
	}

	private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class,
			IdGenerator.class.getName());
	private static final String LAST_TIME_COLUMN_PARAM = "lastTime_column_name";
	private static final String LAST_TIME_COLUMN_DEFAULT = "last_time";
	
	private static final String IDGENERATOR_CACHE_KEY="IdGenerator";
	
	private String lastTimeColumnName;
	private RecycleType recycleType;
	private Date currentDate;
	private Date lastDate;
	private long accessCount;
	
	private Object getPrivateField(String fieldName, Class<?> owner){
		Object result=null;
		try{
			// 得到私有字段  
			Field field=owner.getDeclaredField(fieldName);  
			
			// 通過反射設置私有對象可以訪問  
			field.setAccessible(true);
			result= field.get(this);  
		}catch(Exception e){}
		return result;
	}
	private void setPrivateField(String fieldName, Class<?> owner, Object value){
		try{
			// 得到私有字段  
			Field field=owner.getDeclaredField(fieldName);  
			
			// 通過反射設置私有對象可以訪問  
			field.setAccessible(true);
			field.set(this, value);  
		}catch(Exception e){}
	}
	
	@Override
	public void configure(Type type, Properties params, ServiceRegistry serviceRegistry) throws MappingException {

		recycleType = RecycleType.valueOf(params.getProperty("sequenceValueRecycle").toUpperCase());

		final JdbcEnvironment jdbcEnvironment = serviceRegistry.getService( JdbcEnvironment.class );
		final String name = ConfigurationHelper.getString( LAST_TIME_COLUMN_PARAM, params, LAST_TIME_COLUMN_DEFAULT );
		lastTimeColumnName = jdbcEnvironment.getIdentifierHelper().toIdentifier( name ).render( jdbcEnvironment.getDialect() );

		super.configure(type, params, serviceRegistry);
	}
	
	//id流水号更新周期
	private String getIdIndexUpdCycle(){
		return SysProperties.getInstance().getIdIndexUpdCycle();
//		return CommonContext.getIdIndexUpdCycle();
	}
	private String getIdPrefix(){
		return IdGeneratorHolder.getPrefix();
	}
	private String getSysName(){
		return SysProperties.getInstance().getSysName();
//		return CommonContext.getSysName();
	}
	
	@Override
	public synchronized Serializable generate(final SessionImplementor session, final Object obj) {
		
		//获取ID的更新周期
		String idIdxUpdCycle = getIdIndexUpdCycle();
		idIdxUpdCycle = StringUtils.isBlank(idIdxUpdCycle)?"2000":idIdxUpdCycle;
		
		//缓存Jedis对象
	//	Jedis jedis = JedisUtil.getJedis();
		
		//获取当前数据数字序号
		String xStr = _generate(session,obj,Long.parseLong(idIdxUpdCycle)).toString();
		
		String sysName = "";
		try {
			sysName = getIdPrefix();
		} catch (BaseException e) {
			e.printStackTrace();
		}
		if(StringUtils.isBlank(sysName)){
			sysName = getSysName();
		}
		
		String cDate = getCycleDateStr(currentDate);
		String hexDate = Integer.toHexString(Integer.parseInt(cDate));
		
		String strId = sysName + hexDate + StringUtils.numToStringOnChar62(Integer.parseInt(xStr));	
		return strId;	
	}
	
	private IntegralDataTypeHolder makeValue() {
		return IdentifierGeneratorHelper.getIntegralDataTypeHolder( /*identifierType.getReturnedClass()*/Integer.class );
	}
	
	private Serializable _generate(final SessionImplementor session, final Object obj,long updIdx) {
		final SqlStatementLogger statementLogger = session.getFactory().getServiceRegistry().getService( JdbcServices.class ).getSqlStatementLogger();
		final SessionEventListenerManager statsCollector = session.getEventListenerManager();
		return getOptimizer().generate(
				new AccessCallback() {
					@Override
					public IntegralDataTypeHolder getNextValue() {
						return session.getTransactionCoordinator().createIsolationDelegate().delegateWork(
							new AbstractReturningWork<IntegralDataTypeHolder>() {
								@Override
								public IntegralDataTypeHolder execute(Connection connection) throws SQLException {
									final IntegralDataTypeHolder value = makeValue();
									int rows;
									long oldValue;
									boolean isUpdData = false;
									do {
										final PreparedStatement selectPS = prepareStatement(connection,(String)getPrivateField("selectQuery",TableGenerator.class), statementLogger, statsCollector );
										//当前缓存是否存在
										boolean isCache = false;		
										try {
											selectPS.setString(1, getSegmentValue() );
											final ResultSet selectRS = executeQuery( selectPS, statsCollector );
											if(!selectRS.next()){
												oldValue = (long)getInitialValue();	
												value.initialize( oldValue );
												currentDate = new Date(System.currentTimeMillis());
												final PreparedStatement insertPS = prepareStatement(connection,(String)getPrivateField("insertQuery",TableGenerator.class), statementLogger, statsCollector );
												try {
													insertPS.setString( 1, getSegmentValue() );
													value.bind(insertPS, 2 );
													executeUpdate(insertPS, statsCollector );
												}finally {
													insertPS.close();
												}
												
												String cycleDataStr = getCycleDateStr(currentDate);
												String cacheKey = getSegmentValue().toUpperCase()+"_"+cycleDataStr;
												
												//当前缓存是否存在（必须在初始化之前，因为下面还需这个值进行判断）
												isCache = JedisUtil.exists(IDGENERATOR_CACHE_KEY,cacheKey);
												
												//数据库没有数据的情况，初始缓存
												JedisUtil.set(IDGENERATOR_CACHE_KEY, cacheKey, 1L);
												
												isUpdData = true;	//需更新数据库
												
											}else {
												value.initialize(selectRS, 1);
												oldValue = ((BasicHolder) value).getActualLongValue();
												currentDate = selectRS.getDate(3);
												lastDate=selectRS.getDate(2);
												Long x = validValue(lastDate, currentDate, oldValue);
												
												String cycleDataStr = getCycleDateStr(currentDate);
												String cacheKey = getSegmentValue().toUpperCase()+"_"+cycleDataStr;
												
												//当前缓存是否存在
												isCache = JedisUtil.exists(IDGENERATOR_CACHE_KEY,cacheKey);
												
												//判断缓存是否存在，如果缓存不存在则有可能缓存误删了，则需重新初始化缓存，缓存(流水号)直接加ID数值的更新周期（因为一个周期才能更新一次数据库）,形成新的id序列数值
												if(!isCache){
													x = x + updIdx;
													isUpdData = true;	//需更新数据库，同步最新流水号
													value.initialize(x);
													JedisUtil.set(IDGENERATOR_CACHE_KEY,cacheKey,x);	//初始化缓存
												}
											}
											selectRS.close();
										}catch (SQLException e) {
											LOG.unableToReadOrInitHiValue( e );
											throw e;
										}finally {
											selectPS.close();
										}
										
										//缓存存在的情况,则判断流水号是否达到周期，达到周期则
										if(isCache){
											String cycleDataStr = getCycleDateStr(currentDate);
											String cacheKey = getSegmentValue().toUpperCase()+"_"+cycleDataStr;
											
											Long nubIdx = JedisUtil.incr(IDGENERATOR_CACHE_KEY,cacheKey);	//流水号增1
											value.initialize(nubIdx);
											
											if(nubIdx%updIdx==0){	//达到周期更新数据库
												isUpdData = true;	//需更新数据库，同步最新流水号
											}
										}
										
										//判断是否需要更新数据
										if(isUpdData){
											final PreparedStatement updatePS = prepareStatement(connection,(String)getPrivateField("updateQuery",TableGenerator.class), statementLogger, statsCollector );
											try {
												final IntegralDataTypeHolder updateValue = value.copy();
												/*if(getOptimizer().applyIncrementSizeToSourceValues() ) {
													updateValue.add(getIncrementSize());
												}else {
													updateValue.increment();
												}*/
												//初始化维护当前缓存最新的值
											//	updateValue.initialize(updVal);
												
												updateValue.bind(updatePS,1);
												//value.bind(updatePS, 2);
												updatePS.setLong(2, oldValue);
												updatePS.setString(3, getSegmentValue());
												rows = executeUpdate(updatePS, statsCollector);
												
												
											}catch (SQLException e) {
												LOG.unableToUpdateQueryHiValue( getTableName(), e );
												throw e;
											}finally {
												updatePS.close();
											}
										}else{
											rows = 1;
										}
									}while (rows == 0);

									accessCount++;
									setPrivateField("accessCount",TableGenerator.class,accessCount);//为保证兼容，通过反射设置父类中这个私有变量的值

									return value;
								}

								// 验证流水号，根据周期设置决定使用当前值还是归0;
								// TODO: 还需要处理数字表示不够的情形，将数字进行进制转换---10进制转换为62进制(10个数字字符+52个大小写字母)
								@SuppressWarnings("deprecation")
								private Long validValue(Date lastModifydate,Date curentDate, long value) {
									long result = (long) getInitialValue();
									/**
									 * Java中的数据类型，可分为两类：
									 * 1.基本数据类型，也称原始数据类型。byte,short,char,int,long,float,double,boolean
									 * 他们之间的比较，应用双等号（==）,比较的是他们的值。 2.复合数据类型(类)
									 * 当他们用（==）进行比较的时候，比较的是他们在内存中的存放地址，所以，除非是同一个new出来的对象，他们的比较后的结果为true，否则比较后结果为false。
									 * JAVA当中所有的类都是继承于Object这个基类的，在Object中的基类中定义了一个equals的方法，这个方法的初始行为是比较对象的内存地
									 * 址，但在一些类库当中这个方法被覆盖掉了，如String,Integer,Date在这些类当中equals有其自身的实现，而不再是比较类在堆内存中的存放地址了。
									 * 对于复合数据类型之间进行equals比较，在没有覆写equals方法的情况下，他们之间的比较还是基于他们在内存中的存放位置的地址值的，因为Object的equals方法也是用双等号（==）进行比较的，所以比较后的结果跟双等号（==）的结果相同。
									 */
									switch (recycleType) {
									case DAY:
										String a = DateTimeUtils.format(lastModifydate, "yyyyMMdd");
										String b = DateTimeUtils.format(curentDate,"yyyyMMdd");
										if (a.equals(b))
											result = value;
										break;
									case WEEK:
										if (DateTimeUtils.getWeekNumber(lastModifydate) == DateTimeUtils.getWeekNumber(curentDate))
											result = value;
										break;
									case MONTH:
										if (lastModifydate.getMonth() == curentDate.getMonth())
											result = value;
										break;
									case QUARTER:
										if (DateTimeUtils.getQuarter(lastModifydate) == DateTimeUtils.getQuarter(curentDate))
											result = value;
										break;
									case HALFOONEYEAR:
										if (DateTimeUtils.getHalfOneYear(lastModifydate) == DateTimeUtils.getHalfOneYear(curentDate))
											result = value;
										break;
									case YEAR:
										if (lastModifydate.getYear() == curentDate.getYear())
											result = value;
										break;
									default:
										result = value;
										break;
									}

									return result;
								}
							},
							true
					);
				}

				@Override
				public String getTenantIdentifier() {
					return session.getTenantIdentifier();
				}
			}
		);
	}
	
	
	/**
	 * 获取当前周期的标识 <br>
	 * 
	 * @param curentDate
	 * @return
	 */
	protected String getCycleDateStr(Date curentDate){
		String result = "";
		String year = DateTimeUtils.format(curentDate,"yyyy");
		switch (recycleType) {
		case DAY:
			result = DateTimeUtils.format(curentDate,"yyyyMMdd");
			break;
		case WEEK:
			int week = DateTimeUtils.getWeekNumber(curentDate);
			result = year+"66"+week;
			break;
		case MONTH:
			int month = curentDate.getMonth();
			result = year+"77"+month;
			break;
		case QUARTER:
			int quarter = DateTimeUtils.getQuarter(curentDate);
			result = year+"88"+quarter;
			break;
		case HALFOONEYEAR:
			int half = DateTimeUtils.getHalfOneYear(curentDate);
			result = year+"99"+half;
			break;
		case YEAR:
			result = year;
			break;
		default:
			result = DateTimeUtils.format(curentDate,"yyyyMMdd");
			break;
		}
		return result;
	}
	
	
	@Override
	protected String buildSelectQuery(Dialect dialect) {
		final String alias = "tbl";
		final String query = "select " + StringHelper.qualify(alias, this.getValueColumnName()) + ", "
				+ StringHelper.qualify(alias, lastTimeColumnName) + ", current_date" + " from " + this.getTableName() + ' '
				+ alias + " where " + StringHelper.qualify(alias, this.getSegmentColumnName()) + "=?";
		final LockOptions lockOptions = new LockOptions(LockMode.PESSIMISTIC_WRITE);
		lockOptions.setAliasSpecificLockMode(alias, LockMode.PESSIMISTIC_WRITE);
		final Map<String, String[]> updateTargetColumnsMap = Collections.singletonMap(alias,new String[]{this.getValueColumnName()});
		return dialect.applyLocksToSql(query, lockOptions, updateTargetColumnsMap);
	}

	@Override
	protected String buildUpdateQuery() {
		return "update " + this.getTableName() + " set " + this.getValueColumnName() + "=? , " + lastTimeColumnName + "=current_date"
				+ " where " +  this.getValueColumnName() + "=? and " +  this.getSegmentColumnName() + "=?";
	}

	@Override
	protected String buildInsertQuery() {
		return "insert into " +  this.getTableName() + " (" + lastTimeColumnName + ", " +  this.getSegmentColumnName() + ", "
				+  this.getValueColumnName() + ") " + " values (current_date,?,?)";
	}

	private PreparedStatement prepareStatement(
			Connection connection,
			String sql,
			SqlStatementLogger statementLogger,
			SessionEventListenerManager statsCollector) throws SQLException {
		statementLogger.logStatement( sql, FormatStyle.BASIC.getFormatter() );
		try {
			statsCollector.jdbcPrepareStatementStart();
			return connection.prepareStatement( sql );
		}finally {
			statsCollector.jdbcPrepareStatementEnd();
		}
	}

	private int executeUpdate(PreparedStatement ps, SessionEventListenerManager statsCollector) throws SQLException {
		try {
			statsCollector.jdbcExecuteStatementStart();
			return ps.executeUpdate();
		}finally {
			statsCollector.jdbcExecuteStatementEnd();
		}

	}

	private ResultSet executeQuery(PreparedStatement ps, SessionEventListenerManager statsCollector) throws SQLException {
		try {
			statsCollector.jdbcExecuteStatementStart();
			return ps.executeQuery();
		}finally {
			statsCollector.jdbcExecuteStatementEnd();
		}
	}
}
