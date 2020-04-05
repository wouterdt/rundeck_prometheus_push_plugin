/**
* rundeck prometheus push notifications plugin
*
* sends start epoch time, end epoch time and exit status to a prometheus push gateway
*
* author: jan@tokyoeye.net
*/
import com.dtolabs.rundeck.plugins.notification.NotificationPlugin;
def call = { destination, metric, key, value ->
            def sout = new StringBuilder(), serr = new StringBuilder()
            def proc = ["sh", "-c", "/bin/echo \"${key} ${value}\" | /usr/bin/curl --data-binary @- \"${destination}/metrics/job/${metric}\""].execute()
            proc.waitFor()
            proc.consumeProcessOutput(sout, serr)
            println "prometheus push for $metric to $destination executed:"
            println "out> $sout err> $serr"


                    

    }
rundeckPlugin(NotificationPlugin){
    title="Prometheus Push"
    description="Push metrics to a Prometheus Push Gateway"

    configuration {
         prometheus_hosts (title: "Prometheus push gateways", defaultValue:"prometheus-001:9091,prometheus-002:9091", required: true, description: "Comma seperated list of prometheus push gateways") 
         metric_prefix (defaultValue:"sythetic_")
    }
   
    onstart {
        true
    }

    onfailure {
        def prometheus_list = configuration.prometheus_hosts.split(',')
        prometheus_list.each {
             call(it,configuration.metric_prefix+ execution.job.name, "availability", 0 )
             call(it,configuration.metric_prefix+ execution.job.name, "end", execution.dateEndedUnixtime)
            call(it,configuration.metric_prefix+ execution.job.name, "start", execution.dateStartedUnixtime)
        }
        true
    }

    onsuccess {
        def prometheus_list = configuration.prometheus_hosts.split(',')
        prometheus_list.each {
            call(it,configuration.metric_prefix+ execution.job.name, "availability", 100)
            call(it,configuration.metric_prefix+ execution.job.name, "end", execution.dateEndedUnixtime)
            call(it,configuration.metric_prefix+ execution.job.name, "start", execution.dateStartedUnixtime)
        }
        true
    }
}
