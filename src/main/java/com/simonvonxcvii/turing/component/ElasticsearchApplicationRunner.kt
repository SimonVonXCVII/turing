//package com.simonvonxcvii.turing.component
//
//import co.elastic.clients.elasticsearch.ElasticsearchClient
//import com.simonvonxcvii.turing.entity.OrganizationBusiness
//import com.simonvonxcvii.turing.repository.jpa.OrganizationBusinessRepository
//import org.springframework.boot.ApplicationArguments
//import org.springframework.boot.ApplicationRunner
//import org.springframework.stereotype.Component
//
///**
// * Elasticsearch 数据初始化
// * 用于指示 bean 在包含在 SpringApplication 中时应运行的接口。
// * 可以在同一个应用程序上下文中定义多个 ApplicationRunner bean，并且可以使用 Ordered 接口或 @Order 注解进行排序。
// *
// * @author Simon Von
// * @since 2023/7/16 14:32
// */
//@Component
//class ElasticsearchApplicationRunner(
//    private val elasticsearchClient: ElasticsearchClient,
//    private val organizationBusinessRepository: OrganizationBusinessRepository,
//) : ApplicationRunner {
//    /**
//     * Callback used to run the bean.
//     *
//     * @param args incoming application arguments
//     */
//    override fun run(args: ApplicationArguments) {
//        val organizationBusinessList = organizationBusinessRepository.findAll()
//        if (organizationBusinessList.isEmpty()) {
//            return
//        }
//        elasticsearchClient.bulk { bulkRequest ->
//            organizationBusinessList.forEach { organizationBusiness ->
//                bulkRequest.operations { bulkOperation ->
//                    bulkOperation.create { createOperation ->
//                        createOperation.index(OrganizationBusiness.INDEX)
//                            .id(organizationBusiness.id)
//                            .document(organizationBusiness)
//                    }
//                }
//            }
//            bulkRequest
//        }
//    }
//}
