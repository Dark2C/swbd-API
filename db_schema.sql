SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET AUTOCOMMIT = 0;
START TRANSACTION;
SET time_zone = "+00:00";

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

DROP DATABASE IF EXISTS `swbd`;
CREATE DATABASE IF NOT EXISTS `swbd` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE `swbd`;

CREATE TABLE `anomalie` (
  `ID_anomalia` int(11) NOT NULL,
  `descrizione` text NOT NULL,
  `sensore` int(11) NOT NULL,
  `intervento` int(11) NOT NULL,
  `data_segnalazione` timestamp NOT NULL DEFAULT current_timestamp(),
  `stato` enum('inserito','in corso','risolto','non risolvibile') NOT NULL DEFAULT 'inserito'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `attuatori_impianto` (
  `ID_attuatore_impianto` int(11) NOT NULL,
  `impianto` int(11) NOT NULL,
  `tipologia` int(11) NOT NULL,
  `data_installazione` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `badge` (
  `ID` int(11) NOT NULL,
  `ID_utente` int(11) NOT NULL,
  `ID_impianto` int(11) NOT NULL,
  `data_rilevazione` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `comuni` (
  `sigla_comune` char(2) NOT NULL,
  `nome_comune` char(21) NOT NULL,
  `nome_regione` enum('Sicilia','Piemonte','Marche','Valle d''Aosta','Abruzzo','Toscana','Campania','Puglia','Lombardia','Veneto','Emilia-Romagna','Trentino-Alto Adige','Sardegna','Molise','Calabria','Lazio','Liguria','Friuli-Venezia Giulia','Basilicata','Umbria','Stato Estero') NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO `comuni` (`sigla_comune`, `nome_comune`, `nome_regione`) VALUES
('AG', 'Agrigento', 'Sicilia'),
('AL', 'Alessandria', 'Piemonte'),
('AN', 'ANCONA', 'Marche'),
('AO', 'AOSTA', 'Valle d\'Aosta'),
('AP', 'Ascoli Piceno', 'Marche'),
('AQ', 'L\'AQUILA', 'Abruzzo'),
('AR', 'Arezzo', 'Toscana'),
('AT', 'Asti', 'Piemonte'),
('AV', 'Avellino', 'Campania'),
('BA', 'BARI', 'Puglia'),
('BG', 'Bergamo', 'Lombardia'),
('BI', 'Biella', 'Piemonte'),
('BL', 'Belluno', 'Veneto'),
('BN', 'Benevento', 'Campania'),
('BO', 'BOLOGNA', 'Emilia-Romagna'),
('BR', 'Brindisi', 'Puglia'),
('BS', 'Brescia', 'Lombardia'),
('BT', 'Barletta-Andria-Trani', 'Puglia'),
('BZ', 'Bolzano', 'Trentino-Alto Adige'),
('CA', 'CAGLIARI', 'Sardegna'),
('CB', 'CAMPOBASSO', 'Molise'),
('CE', 'Caserta', 'Campania'),
('CH', 'Chieti', 'Abruzzo'),
('CL', 'Caltanissetta', 'Sicilia'),
('CN', 'Cuneo', 'Piemonte'),
('CO', 'Como', 'Lombardia'),
('CR', 'Cremona', 'Lombardia'),
('CS', 'Cosenza', 'Calabria'),
('CT', 'Catania', 'Sicilia'),
('CZ', 'CATANZARO', 'Calabria'),
('EE', 'Stato Estero', 'Stato Estero'),
('EN', 'Enna', 'Sicilia'),
('FC', 'Forli\'-Cesena', 'Emilia-Romagna'),
('FE', 'Ferrara', 'Emilia-Romagna'),
('FG', 'Foggia', 'Puglia'),
('FI', 'FIRENZE', 'Toscana'),
('FM', 'Fermo', 'Marche'),
('FR', 'Frosinone', 'Lazio'),
('GE', 'GENOVA', 'Liguria'),
('GO', 'Gorizia', 'Friuli-Venezia Giulia'),
('GR', 'Grosseto', 'Toscana'),
('IM', 'Imperia', 'Liguria'),
('IS', 'Isernia', 'Molise'),
('KR', 'Crotone', 'Calabria'),
('LC', 'Lecco', 'Lombardia'),
('LE', 'Lecce', 'Puglia'),
('LI', 'Livorno', 'Toscana'),
('LO', 'Lodi', 'Lombardia'),
('LT', 'Latina', 'Lazio'),
('LU', 'Lucca', 'Toscana'),
('MB', 'Monza', 'Lombardia'),
('MC', 'Macerata', 'Marche'),
('ME', 'Messina', 'Sicilia'),
('MI', 'MILANO', 'Lombardia'),
('MN', 'Mantova', 'Lombardia'),
('MO', 'Modena', 'Emilia-Romagna'),
('MS', 'Massa', 'Toscana'),
('MT', 'Matera', 'Basilicata'),
('NA', 'NAPOLI', 'Campania'),
('NO', 'Novara', 'Piemonte'),
('NU', 'Nuoro', 'Sardegna'),
('OR', 'Oristano', 'Sardegna'),
('PA', 'PALERMO', 'Sicilia'),
('PC', 'Piacenza', 'Emilia-Romagna'),
('PD', 'Padova', 'Veneto'),
('PE', 'Pescara', 'Abruzzo'),
('PG', 'PERUGIA', 'Umbria'),
('PI', 'Pisa', 'Toscana'),
('PN', 'Pordenone', 'Friuli-Venezia Giulia'),
('PO', 'Prato', 'Toscana'),
('PR', 'Parma', 'Emilia-Romagna'),
('PT', 'Pistoia', 'Toscana'),
('PU', 'Pesaro e Urbino', 'Marche'),
('PV', 'Pavia', 'Lombardia'),
('PZ', 'POTENZA', 'Basilicata'),
('RA', 'Ravenna', 'Emilia-Romagna'),
('RC', 'Reggio Calabria', 'Calabria'),
('RE', 'Reggio Emilia', 'Emilia-Romagna'),
('RG', 'Ragusa', 'Sicilia'),
('RI', 'Rieti', 'Lazio'),
('RM', 'ROMA', 'Lazio'),
('RN', 'Rimini', 'Emilia-Romagna'),
('RO', 'Rovigo', 'Veneto'),
('SA', 'Salerno', 'Campania'),
('SI', 'Siena', 'Toscana'),
('SO', 'Sondrio', 'Lombardia'),
('SP', 'La Spezia', 'Liguria'),
('SR', 'Siracusa', 'Sicilia'),
('SS', 'Sassari', 'Sardegna'),
('SU', 'Carbonia', 'Sardegna'),
('SV', 'Savona', 'Liguria'),
('TA', 'Taranto', 'Puglia'),
('TE', 'Teramo', 'Abruzzo'),
('TN', 'TRENTO', 'Trentino-Alto Adige'),
('TO', 'TORINO', 'Piemonte'),
('TP', 'Trapani', 'Sicilia'),
('TR', 'Terni', 'Umbria'),
('TS', 'TRIESTE', 'Friuli-Venezia Giulia'),
('TV', 'Treviso', 'Veneto'),
('UD', 'Udine', 'Friuli-Venezia Giulia'),
('VA', 'Varese', 'Lombardia'),
('VB', 'Verbania', 'Piemonte'),
('VC', 'Vercelli', 'Piemonte'),
('VE', 'VENEZIA', 'Veneto'),
('VI', 'Vicenza', 'Veneto'),
('VR', 'Verona', 'Veneto'),
('VT', 'Viterbo', 'Lazio'),
('VV', 'Vibo Valentia', 'Calabria');

CREATE TABLE `impianti` (
  `ID_impianto` int(11) NOT NULL,
  `nome` varchar(32) NOT NULL,
  `descrizione` text NOT NULL,
  `latitudine` decimal(10,7) NOT NULL,
  `longitudine` decimal(10,7) NOT NULL,
  `comune` char(2) NOT NULL,
  `intervallo_standard` int(11) NOT NULL DEFAULT 300 COMMENT 'espresso in secondi',
  `intervallo_anomalia` int(11) NOT NULL DEFAULT 30 COMMENT 'espresso in secondi'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `impianti_assegnati` (
  `ID_utente` int(11) NOT NULL,
  `ID_impianto` int(11) NOT NULL,
  `permesso_scrittura` tinyint(1) NOT NULL DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `interventi` (
  `ID_intervento` int(11) NOT NULL,
  `data_inserimento` timestamp NOT NULL DEFAULT current_timestamp(),
  `data_inizio` timestamp NULL DEFAULT NULL,
  `data_fine` timestamp NULL DEFAULT NULL,
  `stato` enum('inserito','in corso','risolto','non risolvibile') NOT NULL DEFAULT 'inserito'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `letture` (
  `ID_lettura` int(11) NOT NULL,
  `sensore` int(11) NOT NULL,
  `valore` double NOT NULL,
  `data_inserimento` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `operazioni` (
  `ID_operazione` int(11) NOT NULL,
  `attuatore` int(11) NOT NULL,
  `valore` double NOT NULL,
  `data_inserimento` timestamp NOT NULL DEFAULT current_timestamp(),
  `conferma_lettura` tinyint(1) NOT NULL DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `sensori_impianto` (
  `ID_sensore_impianto` int(11) NOT NULL,
  `impianto` int(11) NOT NULL,
  `tipologia` int(11) NOT NULL,
  `data_installazione` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `sessioni_attive` (
  `token` char(128) NOT NULL,
  `ID_utente` int(11) NOT NULL,
  `data_login` timestamp NOT NULL DEFAULT current_timestamp(),
  `data_ultima_operazione` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `tecnici_intervento` (
  `ID_tecnico_intervento` int(11) NOT NULL,
  `utente` int(11) NOT NULL,
  `intervento` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `tipologie_attuatori` (
  `ID_tipologia_attuatore` int(11) NOT NULL,
  `produttore` varchar(32) NOT NULL,
  `modello` varchar(32) NOT NULL,
  `descrizione` text NOT NULL,
  `tipo_valore` enum('int','float') NOT NULL,
  `valore_min` double NOT NULL,
  `valore_max` double NOT NULL,
  `unita_misura` varchar(16) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `tipologie_sensori` (
  `ID_tipologia_sensore` int(11) NOT NULL,
  `produttore` varchar(32) NOT NULL,
  `modello` varchar(32) NOT NULL,
  `descrizione` text NOT NULL,
  `valore_min` double NOT NULL,
  `valore_max` double NOT NULL,
  `unita_misura` varchar(16) NOT NULL,
  `unita_misura_integrale` varchar(16) DEFAULT NULL,
  `coeff_integrale` double NOT NULL DEFAULT 1
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `utenti` (
  `ID_utente` int(11) NOT NULL,
  `tipologia` enum('amministratore','dipendente','tecnico','monitor') NOT NULL,
  `email` varchar(256) DEFAULT NULL,
  `username` varchar(32) NOT NULL,
  `password` varchar(128) NOT NULL,
  `nome_cognome` varchar(64) DEFAULT NULL,
  `stato_account` enum('disattivato','attivo') NOT NULL DEFAULT 'attivo',
  `comune` char(2) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO `utenti` (`ID_utente`, `tipologia`, `email`, `username`, `password`, `nome_cognome`, `stato_account`, `comune`) VALUES
(1, 'amministratore', NULL, 'admin', '$2a$12$o4TKx0mLSz4XUoab3HGMBe5sFXJxqsZThoPqBvF/L7VJY9xohlPNO', NULL, 'attivo', 'NA');

ALTER TABLE `anomalie`
  ADD PRIMARY KEY (`ID_anomalia`),
  ADD KEY `intervento` (`intervento`),
  ADD KEY `sensore` (`sensore`);

ALTER TABLE `attuatori_impianto`
  ADD PRIMARY KEY (`ID_attuatore_impianto`),
  ADD KEY `tipologia` (`tipologia`),
  ADD KEY `impianto` (`impianto`);

ALTER TABLE `badge`
  ADD PRIMARY KEY (`ID`),
  ADD KEY `ID_utente` (`ID_utente`),
  ADD KEY `ID_impianto` (`ID_impianto`);

ALTER TABLE `comuni`
  ADD PRIMARY KEY (`sigla_comune`);

ALTER TABLE `impianti`
  ADD PRIMARY KEY (`ID_impianto`),
  ADD KEY `comune` (`comune`);

ALTER TABLE `impianti_assegnati`
  ADD PRIMARY KEY (`ID_utente`,`ID_impianto`),
  ADD KEY `ID_impianto` (`ID_impianto`);

ALTER TABLE `interventi`
  ADD PRIMARY KEY (`ID_intervento`);

ALTER TABLE `letture`
  ADD PRIMARY KEY (`ID_lettura`),
  ADD KEY `sensore` (`sensore`);

ALTER TABLE `operazioni`
  ADD PRIMARY KEY (`ID_operazione`),
  ADD KEY `attuatore` (`attuatore`);

ALTER TABLE `sensori_impianto`
  ADD PRIMARY KEY (`ID_sensore_impianto`),
  ADD KEY `tipologia` (`tipologia`),
  ADD KEY `impianto` (`impianto`);

ALTER TABLE `sessioni_attive`
  ADD PRIMARY KEY (`token`),
  ADD KEY `ID_utente` (`ID_utente`);

ALTER TABLE `tecnici_intervento`
  ADD PRIMARY KEY (`ID_tecnico_intervento`),
  ADD UNIQUE KEY `utente_intervento` (`utente`,`intervento`),
  ADD KEY `intervento` (`intervento`);

ALTER TABLE `tipologie_attuatori`
  ADD PRIMARY KEY (`ID_tipologia_attuatore`);

ALTER TABLE `tipologie_sensori`
  ADD PRIMARY KEY (`ID_tipologia_sensore`);

ALTER TABLE `utenti`
  ADD PRIMARY KEY (`ID_utente`),
  ADD KEY `comune` (`comune`);

ALTER TABLE `anomalie`
  MODIFY `ID_anomalia` int(11) NOT NULL AUTO_INCREMENT;

ALTER TABLE `attuatori_impianto`
  MODIFY `ID_attuatore_impianto` int(11) NOT NULL AUTO_INCREMENT;

ALTER TABLE `badge`
  MODIFY `ID` int(11) NOT NULL AUTO_INCREMENT;

ALTER TABLE `impianti`
  MODIFY `ID_impianto` int(11) NOT NULL AUTO_INCREMENT;

ALTER TABLE `interventi`
  MODIFY `ID_intervento` int(11) NOT NULL AUTO_INCREMENT;

ALTER TABLE `letture`
  MODIFY `ID_lettura` int(11) NOT NULL AUTO_INCREMENT;

ALTER TABLE `operazioni`
  MODIFY `ID_operazione` int(11) NOT NULL AUTO_INCREMENT;

ALTER TABLE `sensori_impianto`
  MODIFY `ID_sensore_impianto` int(11) NOT NULL AUTO_INCREMENT;

ALTER TABLE `tecnici_intervento`
  MODIFY `ID_tecnico_intervento` int(11) NOT NULL AUTO_INCREMENT;

ALTER TABLE `tipologie_attuatori`
  MODIFY `ID_tipologia_attuatore` int(11) NOT NULL AUTO_INCREMENT;

ALTER TABLE `tipologie_sensori`
  MODIFY `ID_tipologia_sensore` int(11) NOT NULL AUTO_INCREMENT;

ALTER TABLE `utenti`
  MODIFY `ID_utente` int(11) NOT NULL AUTO_INCREMENT;

ALTER TABLE `anomalie`
  ADD CONSTRAINT `anomalie_ibfk_2` FOREIGN KEY (`intervento`) REFERENCES `interventi` (`ID_intervento`) ON DELETE CASCADE,
  ADD CONSTRAINT `anomalie_ibfk_4` FOREIGN KEY (`sensore`) REFERENCES `sensori_impianto` (`ID_sensore_impianto`) ON DELETE CASCADE;

ALTER TABLE `attuatori_impianto`
  ADD CONSTRAINT `attuatori_impianto_ibfk_1` FOREIGN KEY (`tipologia`) REFERENCES `tipologie_attuatori` (`ID_tipologia_attuatore`),
  ADD CONSTRAINT `attuatori_impianto_ibfk_2` FOREIGN KEY (`impianto`) REFERENCES `impianti` (`ID_impianto`) ON DELETE CASCADE;

ALTER TABLE `badge`
  ADD CONSTRAINT `badge_ibfk_1` FOREIGN KEY (`ID_utente`) REFERENCES `utenti` (`ID_utente`) ON DELETE CASCADE,
  ADD CONSTRAINT `badge_ibfk_2` FOREIGN KEY (`ID_impianto`) REFERENCES `impianti` (`ID_impianto`) ON DELETE CASCADE;

ALTER TABLE `impianti`
  ADD CONSTRAINT `impianti_ibfk_1` FOREIGN KEY (`comune`) REFERENCES `comuni` (`sigla_comune`);

ALTER TABLE `impianti_assegnati`
  ADD CONSTRAINT `impianti_assegnati_ibfk_1` FOREIGN KEY (`ID_utente`) REFERENCES `utenti` (`ID_utente`) ON DELETE CASCADE,
  ADD CONSTRAINT `impianti_assegnati_ibfk_2` FOREIGN KEY (`ID_impianto`) REFERENCES `impianti` (`ID_impianto`) ON DELETE CASCADE;

ALTER TABLE `letture`
  ADD CONSTRAINT `letture_ibfk_1` FOREIGN KEY (`sensore`) REFERENCES `sensori_impianto` (`ID_sensore_impianto`) ON DELETE CASCADE;

ALTER TABLE `operazioni`
  ADD CONSTRAINT `operazioni_ibfk_1` FOREIGN KEY (`attuatore`) REFERENCES `attuatori_impianto` (`ID_attuatore_impianto`) ON DELETE CASCADE;

ALTER TABLE `sensori_impianto`
  ADD CONSTRAINT `sensori_impianto_ibfk_1` FOREIGN KEY (`tipologia`) REFERENCES `tipologie_sensori` (`ID_tipologia_sensore`),
  ADD CONSTRAINT `sensori_impianto_ibfk_2` FOREIGN KEY (`impianto`) REFERENCES `impianti` (`ID_impianto`) ON DELETE CASCADE;

ALTER TABLE `sessioni_attive`
  ADD CONSTRAINT `sessioni_attive_ibfk_1` FOREIGN KEY (`ID_utente`) REFERENCES `utenti` (`ID_utente`) ON DELETE CASCADE;

ALTER TABLE `tecnici_intervento`
  ADD CONSTRAINT `tecnici_intervento_ibfk_1` FOREIGN KEY (`utente`) REFERENCES `utenti` (`ID_utente`) ON DELETE CASCADE,
  ADD CONSTRAINT `tecnici_intervento_ibfk_2` FOREIGN KEY (`intervento`) REFERENCES `interventi` (`ID_intervento`) ON DELETE SET NULL;

ALTER TABLE `utenti`
  ADD CONSTRAINT `utenti_ibfk_2` FOREIGN KEY (`comune`) REFERENCES `comuni` (`sigla_comune`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
