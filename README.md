# rundeck_prometheus_push_plugin
Rundeck notification plugin that sends simple metrics to prometheus push gateway on job start, success and failure.

The following metrics are sent to prometheus push gateway(s):
- on job start:
  - the epoch start time (prometheus with metric name 'rundeck_start' with the job's name as label)
- on job success:
  - the epoch end time (prometheus metric name 'rundeck_end' with the job's name as label)
  - the exit status as 0 (prometheus metric name 'rundeck_exit' with the job's name as label)
- on job failure:
  - the epoch end time (prometheus metric name 'rundeck_end' with the job's name as label)
  - the exit status as 1 (prometheus metric name 'rundeck_exit' with the job's name as label)


## How to install the plugin
1. drop rundeck-prometheus-push-0.0.1.groovy in /var/lib/rundeck/libext/
2. restart rundeck
3. enable the notification for your job and set the prometheus gateway(s) to use
