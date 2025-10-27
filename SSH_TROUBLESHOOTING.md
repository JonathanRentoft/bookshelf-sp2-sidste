# 🔧 SSH Forbindelsesproblemer

## Problem
SSH forbinder ikke - ingen password prompt vises.

## Trin 1: Test om serveren er oppe

```bash
# Test om serveren svarer
ping 161.35.82.205

# Skal vise:
# 64 bytes from 161.35.82.205: icmp_seq=1 ttl=54 time=20.5 ms
```

**Hvis ingen respons:**
- Serveren er nede eller netværksforbindelsen er blokeret
- Check DigitalOcean dashboard: https://cloud.digitalocean.com/

## Trin 2: Test SSH port

```bash
# Test om SSH porten (22) er åben
nc -zv 161.35.82.205 22

# Eller:
telnet 161.35.82.205 22

# Forventet output:
# Connection to 161.35.82.205 22 port [tcp/ssh] succeeded!
```

**Hvis connection refused eller timeout:**
- SSH service er ikke kørende
- Firewall blokerer port 22
- Du skal bruge DigitalOcean Console til at fikse det

## Trin 3: Tvungen timeout (stop hængende forbindelse)

```bash
# Afbryd den hængende SSH session
# Tryk: Enter ~ .
# (Enter, tilde, punktum)

# Eller luk terminalen og åbn en ny
```

## Trin 4: SSH med verbose mode (se hvad der sker)

```bash
ssh -vvv root@161.35.82.205
```

Dette viser dig præcist hvor forbindelsen fejler.

**Send mig output** af denne kommando!

## Trin 5: Prøv med timeout

```bash
# Prøv med connection timeout
ssh -o ConnectTimeout=10 root@161.35.82.205
```

Hvis det timer out, er serveren/SSH ikke tilgængelig.

## Løsning 1: Brug DigitalOcean Console (WEB)

1. **Gå til DigitalOcean:**
   https://cloud.digitalocean.com/droplets

2. **Find din droplet** (161.35.82.205)

3. **Klik "Console"** (øverst til højre)

4. **Log ind direkte i browser-konsollen**
   - Username: `root`
   - Password: [dit root password]

5. **Check SSH service:**
   ```bash
   systemctl status ssh
   systemctl status sshd
   
   # Hvis ikke running:
   systemctl restart ssh
   systemctl enable ssh
   ```

6. **Check firewall:**
   ```bash
   ufw status
   
   # Hvis SSH er blokeret:
   ufw allow 22/tcp
   ufw reload
   ```

7. **Check om SSH lytter:**
   ```bash
   netstat -tlnp | grep :22
   # Eller:
   ss -tlnp | grep :22
   ```

## Løsning 2: Genstart Serveren

I DigitalOcean dashboard:
1. Vælg din droplet
2. Klik "Power" → "Reboot"
3. Vent 1-2 minutter
4. Prøv SSH igen

## Løsning 3: Check SSH Config på Serveren

Via DigitalOcean Console:

```bash
# Check SSH config
cat /etc/ssh/sshd_config | grep -E "Port|PermitRootLogin|PasswordAuthentication"

# Skal vise:
# Port 22
# PermitRootLogin yes
# PasswordAuthentication yes
```

Hvis noget er forkert:
```bash
nano /etc/ssh/sshd_config

# Sørg for:
Port 22
PermitRootLogin yes
PasswordAuthentication yes

# Gem og genstart SSH:
systemctl restart ssh
```

## Løsning 4: Check om Server Har Hukommelse/Disk

Via DigitalOcean Console:

```bash
# Check disk space
df -h

# Check memory
free -h

# Check system load
uptime
```

Hvis disk er fuld (100%):
```bash
# Ryd Docker
docker system prune -af
```

## Almindelige Årsager

1. **SSH service er stoppet**
   - Fix: `systemctl restart ssh`

2. **Firewall blokerer port 22**
   - Fix: `ufw allow 22/tcp`

3. **Server er løbet tør for hukommelse**
   - Fix: Genstart eller opgradér droplet

4. **SSH config er ødelagt**
   - Fix: Ret `/etc/ssh/sshd_config`

5. **For mange fejlslagne login forsøg (fail2ban)**
   - Fix: Vent 10 minutter eller brug DigitalOcean Console

## Quick Fix Checklist

Fra DigitalOcean Console:

```bash
# 1. Genstart SSH
systemctl restart ssh

# 2. Check firewall
ufw allow 22/tcp
ufw reload

# 3. Check hvis SSH kører
systemctl status ssh

# 4. Check logs
tail -n 50 /var/log/auth.log

# 5. Test lokalt
ssh root@localhost
```

## Output at Sende Til Mig

Kør disse på din lokale maskine og send output:

```bash
# 1. Test forbindelse
ping -c 4 161.35.82.205

# 2. Test SSH port
nc -zv 161.35.82.205 22

# 3. SSH med verbose
ssh -vvv root@161.35.82.205
```

## Alternativ: SSH via Port Forwarding

Hvis du har VPN eller anden adgang:

```bash
# Prøv med IP som hostname
ssh root@161.35.82.205

# Prøv med specifik port
ssh -p 22 root@161.35.82.205

# Prøv med timeout
ssh -o ConnectTimeout=30 root@161.35.82.205
```

## Kontakt DigitalOcean Support

Hvis intet virker:
1. https://cloud.digitalocean.com/support
2. Beskriv problemet
3. De kan hjælpe via console access
