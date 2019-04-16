ImportSelector与自定义@EnableXXX注解
========================

## 实现路径
第一种
```
class: **Startup**
->interface:  **@EnableXfLibImportSelector**
     -> class: **XfLibImportSelector**  implements ImportSelector 
          -> method: selectImports返回需要自动装载的全路径类名数组
```
通过@EnableXfLibImportSelector自动注册XfLibImportSelector，然后注册XflibService。
注意：这种方法的特点是XfLibImportSelector不需要被@ComponentScan扫描

第2种: 利用@ComponentScan
```
class: **Startup**
->interface:  **@SpringBootApplication**
     -> config: **XflibImportSelectorConfig**
         -> class: **XfLibImportSelector**  implements ImportSelector 
              -> method: selectImports返回需要自动装载的全路径类名数组

```
通过@ComponentScan(basePackages = { "com.xflib.*.configuration", })自动注册XflibImportSelectorConfig，然后注册XfLibImportSelector，最后注册XflibService。

**当然，如果XflibImportSelectorConfig的包路径在主类Startup包路径之下，XflibImportSelectorConfig是不需要的，因为Spring boot在没有使用@ComponentScan指定basePackages时会自动搜索主类包路径。**

特别注意：在本例中，XfLibImportSelectorImpl并沒有使用如@Service之类的注解哦！这也是bean的多种注册方式中的2种而已
         

## ImportSelector说明

ImportSelector接口文档上说的明明白白，其主要作用是收集需要导入的配置类，如果该接口的实现类同时实现EnvironmentAware， BeanFactoryAware ，BeanClassLoaderAware或者ResourceLoaderAware，那么在调用其selectImports方法之前先调用上述接口中对应的方法;
如果需要在所有的@Configuration处理完后再导入(延迟處理)时可以实现DeferredImportSelector接口。

*例如：*
```
public class SpringStudySelector implements ImportSelector, BeanFactoryAware {
    private BeanFactory beanFactory;

    @Override
    public String[] selectImports(AnnotationMetadata importingClassMetadata) {
        importingClassMetadata.getAnnotationTypes().forEach(System.out::println);
        System.out.println(beanFactory);
        return new String[]{AppConfig.class.getName()};
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }
}

```
