package com.shiminfxcvii.turing.component

import co.elastic.clients.elasticsearch.ElasticsearchClient
import co.elastic.clients.elasticsearch.core.BulkRequest
import co.elastic.clients.elasticsearch.core.bulk.BulkOperation
import co.elastic.clients.elasticsearch.core.bulk.CreateOperation
import com.shiminfxcvii.turing.entity.OrganizationBusiness
import com.shiminfxcvii.turing.mapper.OrganizationBusinessMapper
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.stereotype.Component
import java.util.function.Consumer

/**
 * Elasticsearch 数据初始化
 * 用于指示 bean 在包含在 SpringApplication 中时应运行的接口。
 * 可以在同一个应用程序上下文中定义多个 ApplicationRunner bean，并且可以使用 Ordered 接口或 @Order 注解进行排序。
 *
 * @author ShiminFXCVII
 * @since 2023/7/16 14:32
 */
@Component
class ElasticsearchApplicationRunner(
    private val elasticsearchClient: ElasticsearchClient,
    private val organizationBusinessMapper: OrganizationBusinessMapper
) : ApplicationRunner {
    /**
     * Callback used to run the bean.
     *
     * @param args incoming application arguments
     * @throws Exception on error
     */
    @Throws(Exception::class)
    override fun run(args: ApplicationArguments) {
        elasticsearchClient.bulk { bulkRequest: BulkRequest.Builder ->
            organizationBusinessMapper.selectList(null)
                .forEach(
                    Consumer { organizationBusiness: OrganizationBusiness ->
                        bulkRequest.operations { bulkOperation: BulkOperation.Builder ->
                            bulkOperation.create { createOperation: CreateOperation.Builder<Any?> ->
                                createOperation.index(OrganizationBusiness.INDEX)
                                    .id(organizationBusiness.id)
                                    .document(organizationBusiness)
                            }
                        }
                    }
                )
            bulkRequest
        }
    }
}