import java.util.Comparator;
import java.util.List;

public class SJF {
    public void run(List<Process> jobList, List<Result> resultList) {
        int currentTime = 0;
        int totalWaitingTime = 0;
        int totalTurnaroundTime = 0;
        int completedProcesses = 0;

        while (completedProcesses < jobList.size()) {
            // 현재 시간 이전에 도착한 프로세스 중에서 완료되지 않은 프로세스를 선택
            Process shortestJob = null;
            for (Process process : jobList) {
                if (!process.isCompleted() && process.getArriveTime() <= currentTime) {
                    if (shortestJob == null || process.getBurstTime() < shortestJob.getBurstTime()) {
                        shortestJob = process;
                    }
                }
            }

            if (shortestJob == null) {
                currentTime++;
                continue;
            }

            // 해당 프로세스를 실행
            int startTime = currentTime;
            int completionTime = startTime + shortestJob.getBurstTime();
            int turnaroundTime = completionTime - shortestJob.getArriveTime();
            int waitingTime = startTime - shortestJob.getArriveTime();
            int responseTime = waitingTime;  // 비선점형 SJF에서는 waiting time이 response time과 동일

            totalWaitingTime += waitingTime;
            totalTurnaroundTime += turnaroundTime;

            // 결과 리스트에 추가
            resultList.add(new Result(shortestJob.getProcessId(), shortestJob.getBurstTime(), waitingTime, turnaroundTime, responseTime));

            // 프로세스 완료 표시
            shortestJob.setCompleted(true);
            completedProcesses++;
            currentTime = completionTime;
        }

        double averageWaitingTime = (double) totalWaitingTime / jobList.size();
        double averageTurnaroundTime = (double) totalTurnaroundTime / jobList.size();
        printResults(averageWaitingTime, currentTime, averageTurnaroundTime, resultList);
    }

    private void printResults(double averageWaitingTime, int cpuExecutionTime, double averageTurnaroundTime, List<Result> resultList) {
        System.out.println("=================================================================");
        System.out.println("SJF 결과");
        System.out.println("평균 대기 시간 : " + averageWaitingTime);
        System.out.println("평균 Turnaround Time : " + averageTurnaroundTime);
        System.out.println("CPU 실행 시간 : " + cpuExecutionTime);

        resultList.sort(Comparator.comparingInt(Result::getProcessID));

        resultList.forEach(result -> {
            System.out.println("----------------------------------------------------------------");
            System.out.println("Process " + result.getProcessID() + "의 waiting time : " + result.getWaitingTime());
            System.out.println("Process " + result.getProcessID() + "의 turnaround Time : " + result.getTurnaroundTime());
            System.out.println("Process " + result.getProcessID() + "의 response Time : " + result.getResponseTime());
        });

        System.out.println("=================================================================");
    }
}
