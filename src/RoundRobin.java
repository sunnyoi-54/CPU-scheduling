import java.util.*;

public class RoundRobin {
    public void run(List<Process> jobList, List<Result> resultList, int timeQuantum) {
        int currentTime = 0;
        int totalWaitingTime = 0;
        int completedProcesses = 0;
        int totalTurnaroundTime = 0;
        int totalResponseTime = 0;
        Queue<Process> processQueue = new LinkedList<>();

        // 도착 시간 기준으로 프로세스 리스트 정렬
        jobList.sort(Comparator.comparingInt(Process::getArriveTime));

        while (completedProcesses < jobList.size()) {
            // 현재 시간까지 도착한 프로세스를 큐에 추가
            for (Process p : jobList) {
                if (p.getArriveTime() <= currentTime && !p.isCompleted() && !processQueue.contains(p)) {
                    processQueue.add(p);
                }
            }

            if (processQueue.isEmpty()) {
                // 도착한 프로세스가 없는 경우, 현재 시간을 증가시킴
                currentTime++;
                continue;
            }

            // 큐에서 프로세스를 꺼내어 처리
            Process currentProcess = processQueue.poll();

            if (currentProcess.getRemainingTime() == currentProcess.getBurstTime()) { // 첫 응답이 시작하는 시간이 응답 시간
                currentProcess.setResponseTime(currentTime - currentProcess.getArriveTime());
                totalResponseTime += currentProcess.getResponseTime();
            }
            int executeTime = Math.min(currentProcess.getRemainingTime(), timeQuantum);
            currentProcess.setRemainingTime(currentProcess.getRemainingTime() - executeTime);
            currentTime += executeTime;

            // 프로세스가 완료된 경우
            if (currentProcess.getRemainingTime() == 0) {
                int arriveTime = currentProcess.getArriveTime();
                int burstTime = currentProcess.getBurstTime();
                int waitingTime = currentTime - arriveTime - burstTime;
                totalWaitingTime += waitingTime;
                int turnaroundTime = waitingTime + burstTime;
                totalTurnaroundTime += turnaroundTime;

                resultList.add(new Result(currentProcess.getProcessId(), burstTime, waitingTime, turnaroundTime, currentProcess.getResponseTime()));
                currentProcess.setCompleted(true);
                completedProcesses++;
            } else {
                // 프로세스가 완료되지 않은 경우 다시 큐에 추가
                processQueue.add(currentProcess);
            }
        }

        double averageWaitingTime = (double) totalWaitingTime / jobList.size();
        double averageTurnaroundTime = (double) totalTurnaroundTime / jobList.size();
        double averageResponseTime = (double) totalResponseTime / jobList.size();

        printResults(averageWaitingTime, currentTime, averageTurnaroundTime, averageResponseTime, resultList);
    }


    private static void printResults(double averageWaitingTime, int cpuExecutionTime, double averageTurnaroundTime, double averageResponseTime, List<Result> resultList) {
        System.out.println("=================================================================");
        System.out.println("RR 결과");
        System.out.println("평균 대기 시간 : " + averageWaitingTime);
        System.out.println("평균 Turnaround Time : " + averageTurnaroundTime);
        System.out.println("평균 응답 시간 : " + averageResponseTime);
        System.out.println("CPU 실행 시간 : " + cpuExecutionTime);

        resultList.sort(Comparator.comparingInt(Result::getProcessID));

        resultList.forEach(result -> {
            System.out.println("Process" + result.getProcessID() + "의 waiting time : " + result.getWaitingTime() +", turnaround Time : " + result.getTurnaroundTime() +", response Time : " + result.getResponseTime());
        });
    }
}

