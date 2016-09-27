/**
* rundeck prometheus push notifications plugin
*
* sends start epoch time, end epoch time and exit status to a prometheus push gateway
*
* author: jan@tokyoeye.net
*/
import com.dtolabs.rundeck.plugins.notification.NotificationPlugin;

rundeckPlugin(NotificationPlugin){
    title="Prometheus Push"
    description="Push metrics to a Prometheus Push Gateway"

    configuration {
         prometheus_hosts title: "Prometheus push gateways", defaultValue:"prometheus-001:9091,prometheus-002:9091", required: true, description: "Comma seperated list of prometheus push gateways"
    }

    onstart {
        def prometheus_list = configuration.prometheus_hosts.split(',')
        prometheus_list.each {
            def proc = ["sh", "-c", "/bin/echo \"rundeck_start ${execution.dateStartedUnixtime}\" | /usr/bin/curl --data-binary @- http://${it}/metrics/job/${execution.job.name}"].execute()
            proc.waitFor()
        }
        true
    }

    onfailure {
        def prometheus_list = configuration.prometheus_hosts.split(',')
        prometheus_list.each {
            def proc = ["sh", "-c", "/bin/echo \"rundeck_stop ${execution.dateEndedUnixtime}\" | /usr/bin/curl --data-binary @- http://${it}/metrics/job/${execution.job.name}"].execute()
            proc.waitFor()
            def proc_exit = ["sh", "-c", "/bin/echo \"rundeck_exit 1\" | /usr/bin/curl --data-binary @- http://${it}/metrics/job/${execution.job.name}"].execute()
            proc_exit.waitFor()
        }
        true
    }

    onsuccess {
        def prometheus_list = configuration.prometheus_hosts.split(',')
        prometheus_list.each {
            def proc = ["sh", "-c", "/bin/echo \"rundeck_stop ${execution.dateEndedUnixtime}\" | /usr/bin/curl --data-binary @- http://${it}/metrics/job/${execution.job.name}"].execute()
            proc.waitFor()
            def proc_exit = ["sh", "-c", "/bin/echo \"rundeck_exit 0\" | /usr/bin/curl --data-binary @- http://${it}/metrics/job/${execution.job.name}"].execute()
            proc_exit.waitFor()
        }
        true
    }
}
