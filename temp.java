import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.QueryStringQuery;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;

import java.io.IOException;
import java.util.Map;

public class ElasticKqlLikeSearch {
    public static void main(String[] args) throws IOException {
        // Connect to Elasticsearch
        RestClient restClient = RestClient.builder(new HttpHost("localhost", 9200)).build();
        ElasticsearchClient client = new ElasticsearchClient(
                new RestClientTransport(restClient, new JacksonJsonpMapper()));

        // Equivalent to: status:200 AND extension:jpg
        Query query = Query.of(q -> q
                .queryString(QueryStringQuery.of(qs -> qs
                        .query("status:200 AND extension:jpg")
                ))
        );

        // Create search request
        SearchRequest searchRequest = SearchRequest.of(s -> s
                .index("your-index-name") // Replace with your index
                .query(query)
        );

        // Execute search
        SearchResponse<Map> response = client.search(searchRequest, Map.class);

        // Print results
        for (Hit<Map> hit : response.hits().hits()) {
            System.out.println(hit.source());
        }

        restClient.close();
    }
}
