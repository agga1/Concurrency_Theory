**Zad 1.**  
Implementacja semafora binarnego: BinarySemaphore
Synchronizacja programu wyścig: Task1

**Zad 2. Dlaczego if nie wystarczy?**  
Potrzebna jest instrukcja while, ponieważ może się zdarzyć, że wątek został obudzony,  
a dany warunek nadal nie jest spełniony (np. wątek czeka w kilku kolejkach i otrzymał sygnał notify() z jednej z nich,  
podczas gdy warunki do pozostałych kolejek nie są spełnione)
Eksperyment: Task2 (z użyciem BinarySemaphore i BinarySemaphoreIf)

**Zad. 3**  
semafor licznikowy: Semaphore
przykład użycia: Task3_1
użycie semafora licznikowego z licznikiem 1 jako semafora binarnego: Task3_2
wnioski:
Semafor binarny jest szczególnym przypadkiem semafora licznikowego, gdyż zapewnia wzajemne wykluczenie