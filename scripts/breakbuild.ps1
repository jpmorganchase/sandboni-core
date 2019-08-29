$SONAR_PROJECT_KEY = "com.github.jpmorganchase.sandboni%3Asandboni-core"
$SONAR_TOKEN = "f4084d177e7f861ad3e8a6addad8b612b3c62a8d"

Write-Host "Script => Project key: $SONAR_PROJECT_KEY, token: $SONAR_TOKEN"
if (!$SONAR_PROJECT_KEY -or !$SONAR_TOKEN) {
     Write-Error "No SonarCloud project key or token"
     exit 1
}

$SUCCESS_TASK = "SUCCESS"

function Api-Call($url) {
    Write-Host "Script => Calling url: $url"
    $token = [System.Text.Encoding]::UTF8.GetBytes($SONAR_TOKEN + ":")
    $base64 = [System.Convert]::ToBase64String($token)
    $basicAuth = [string]::Format("Basic {0}", $base64)
    $headers = @{ Authorization = $basicAuth }

    return Invoke-RestMethod -Uri $url -Headers $headers
}

function Get-Latest-Task {
    $api_ce_activity = "https://sonarcloud.io/api/ce/activity?component=$SONAR_PROJECT_KEY&type=REPORT&format=JSON"
    $jsonResponse = Api-Call($api_ce_activity)
    return $jsonResponse.tasks[0]
}

function Get-Task-Id($task) {
    $taskId = $latestTask.id
    $status = $latestTask.status
    Write-Host "Script => SonarCloud taskId: $taskId"
    if ($status -ne $SUCCESS_TASK) {
         Write-Error "Task $taskId wasn't completed successfully"
         exit 1
    }
    return $taskId
}

function Get-Task-Status($taskId) {
    $TASK_RESULT = "https://sonarcloud.io/api/ce/task?id=$taskId"
    $taskResult = (makeApiCall($TASK_RESULT)).task
    return $taskResult.status
}

$task = Get-Latest-Task
$taskId = Get-Task-Id($task)
$taskStatus = Get-Task-Status($taskId)

if ($taskStatus -ne $SUCCESS_TASK) {
     Write-Error "Task wasn't completed successfully: $taskId"
     exit 1
}
exit 0