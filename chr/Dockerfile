FROM ubuntu:22.04

RUN apt-get update && \
    apt-get install -y qemu-system-x86 wget unzip && \
    apt-get clean

RUN wget https://download.mikrotik.com/routeros/6.49.18/chr-6.49.18.vmdk.zip && \
    unzip chr-6.49.18.vmdk.zip && \
    rm chr-6.49.18.vmdk.zip && \
    qemu-img convert -f vmdk -O qcow2 chr-6.49.18.vmdk chr.qcow2 && \
    rm chr-6.49.18.vmdk

EXPOSE 22 8291 161/udp

CMD ["qemu-system-x86_64", \
     "-m", "256", \
     "-smp", "1", \
     "-drive", "file=/chr.qcow2,format=qcow2,if=virtio", \
     "-nographic", \
     "-netdev", "user,id=net0,hostfwd=udp::161-:161,hostfwd=tcp::8291-:8291,hostfwd=tcp::22-:22", \
     "-device", "virtio-net-pci,netdev=net0"]