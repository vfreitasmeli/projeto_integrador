# Projeto_Integrador(Requisito 6) Pessoal
API REST desenvolvida pelo grupo Beta Campers para o Projeto Integrador feito durante o IT Bootcamp Backend Java (wave 6). Solução individual para criação de dashboards. 

## Autores
<a href="https://github.com/vfreitasmeli">
  <img src="https://avatars.githubusercontent.com/u/107959338?s=50&v=4" style="width: 50px">
</a>

# Sumário

- [Observações](#observações)
- [Funcionalidades](#funcionalidades)
- Criação de dashboard para a área de de armazém (warehouse), utilizando Logstash como pipeline para capturar os dados vindos da API, salvando-os em document no ElasticSearch e transformando os dados através do Kibana para dashboard com melhor visualização. 

# Funcionalidades

## Logstash: configuração 

- Instalação do Logstash e configuração do arquivo logstash.conf.
<pre><code><b>logstash.conf exemplo:</b>
input {

   jdbc {
    jdbc_driver_library => "<path>/mysql-connector-java-8.0.30/mysql-connector-java-8.0.30.jar"
    jdbc_driver_class => "com.mysql.jdbc.Driver"
    jdbc_connection_string => "jdbc:mysql://localhost:3306/<dbase>"
    jdbc_user => root
    jdbc_password => ""
    jdbc_paging_enabled => true
    tracking_column => "batch_number"
    use_column_value => true
    schedule => "* * * * *"
    statement => <mysql query to filter data>
	}
}

output {
  elasticsearch {
    hosts => ["http://localhost:9200"]
    index => "pi_logstash_analytics_warehouse"
  }
 stdout { codec => rubydebug }
	
}
 </code></pre>
 
 - Com os dados filtrados e index criado no ElasticSearch:
 
 `GET pi_logstash_analytics_warehouse/_doc/JV1Ep4IBjXuA3Mn2zWQZ`
Mostra a resposta de um dos nós do index.
<pre><code><b>Response:</b>
{
  "_index" : "pi_logstash_analytics_warehouse",
  "_type" : "_doc",
  "_id" : "JV1Ep4IBjXuA3Mn2zWQZ",
  "_version" : 1,
  "_seq_no" : 18,
  "_primary_term" : 1,
  "found" : true,
  "_source" : {
    "due_date" : "2024-08-20T03:00:00.000Z",
    "category" : "CHILLED",
    "@timestamp" : "2022-08-16T15:28:00.160Z",
    "current_quantity" : 4,
    "batch_number" : 6,
    "order_date" : "2022-08-16T03:00:00.000Z",
    "manufacturing_date" : "2022-07-25T03:00:00.000Z",
    "product_price" : 10.99,
    "quantity" : 4,
    "section_code" : 6,
    "location" : "Qatar",
    "product_name" : "Iogurte",
    "@version" : "1"
  }
}
  </code></pre>
 - Com as informações obtidas, cria-se o dashboard
