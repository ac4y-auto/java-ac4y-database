# java-ac4y-database - Architektúra Dokumentáció

## Áttekintés

Az `ac4y-database` modul adatbázis adapter és kapcsolat kezelő réteg az Ac4y projektekhez. JDBC alapú adatbázis kapcsolatokat kezel properties fájlokból vagy direkt paraméterezéssel.

**Verzió:** 1.0.0
**Java verzió:** 11
**Szervezet:** ac4y-auto

## Fő Komponensek

### 1. Adatbázis Adapter

#### `Ac4yDBAdapter`
Egyszerű wrapper osztály JDBC Connection objektum körül.

**Felelősség:**
- Connection objektum tárolása
- Connection életciklus menedzsment támogatása

**Konstruktorok:**
- `Ac4yDBAdapter()`: Üres adapter
- `Ac4yDBAdapter(Connection connection)`: Inicializált adapter

**Metódusok:**
- `getConnection()`: JDBC Connection lekérése
- `setConnection(Connection connection)`: Connection beállítása

**Használat:**
```java
Connection conn = // ... obtain connection
Ac4yDBAdapter adapter = new Ac4yDBAdapter(conn);

// Később
Connection c = adapter.getConnection();
PreparedStatement ps = c.prepareStatement("SELECT * FROM users");
```

**Megjegyzés:** Ez egy egyszerű wrapper, a tényleges query végrehajtás a JDBC API-n keresztül történik.

### 2. Adatbázis Kapcsolat

#### `DBConnection`
Properties-alapú vagy paraméteres adatbázis kapcsolat létrehozó osztály.

**Felelősség:**
- JDBC driver betöltése
- Connection string, user, password kezelése
- Connection létrehozása és tárolása
- Properties fájlból történő konfigurálás

**Konstruktorok:**

1. **Paraméteres konstruktor:**
```java
public DBConnection(String driver, String connectionString,
                    String host, String user, String password)
    throws ClassNotFoundException, SQLException
```

2. **Default properties konstruktor (ac4y.properties):**
```java
public DBConnection()
    throws ClassNotFoundException, SQLException, IOException, Ac4yException
```

3. **Nevesített properties konstruktor:**
```java
public DBConnection(String propertiesName)
    throws ClassNotFoundException, SQLException, IOException, Ac4yException
```

4. **Properties objektum konstruktor:**
```java
public DBConnection(Properties properties)
    throws ClassNotFoundException, SQLException
```

**Properties Formátum:**

A properties fájlnak az alábbi kulcsokat kell tartalmaznia:
```properties
driver=com.mysql.cj.jdbc.Driver
connectionString=jdbc:mysql://localhost:3306/mydb?useSSL=false&serverTimezone=UTC
dbuser=root
dbpassword=secret123
```

**Példák:**

1. **Paraméterekkel:**
```java
DBConnection dbConn = new DBConnection(
    "com.mysql.cj.jdbc.Driver",
    "jdbc:mysql://localhost:3306/mydb",
    "localhost",
    "admin",
    "password"
);
Connection conn = dbConn.getConnection();
```

2. **Default properties-ből (ac4y.properties):**
```java
DBConnection dbConn = new DBConnection();
Connection conn = dbConn.getConnection();
```

3. **Nevesített properties-ből:**
```java
DBConnection dbConn = new DBConnection("mymodule.properties");
Connection conn = dbConn.getConnection();
```

4. **Properties objektumból:**
```java
Properties props = new Properties();
props.setProperty("driver", "org.postgresql.Driver");
props.setProperty("connectionString", "jdbc:postgresql://localhost/mydb");
props.setProperty("dbuser", "admin");
props.setProperty("dbpassword", "secret");

DBConnection dbConn = new DBConnection(props);
Connection conn = dbConn.getConnection();
```

**Belső Működés:**

1. **Driver betöltés:**
```java
Class.forName(driver); // JDBC driver regisztrálása
```

2. **Connection létrehozás:**
```java
connection = DriverManager.getConnection(connectionString, user, password);
```

3. **Properties feldolgozás:**
```java
ExternalPropertyHandler propHandler = new ExternalPropertyHandler();
Properties props = propHandler.getPropertiesFromClassPath(propertiesName);
setAttributesFromProperties(props);
```

**Attribútumok:**
- `driver` (String): JDBC driver osztály neve
- `host` (String): Adatbázis host (jelenleg nem használt a connection létrehozásában)
- `user` (String): Adatbázis felhasználó
- `password` (String): Adatbázis jelszó
- `connectionString` (String): Teljes JDBC connection string
- `connection` (Connection): JDBC kapcsolat objektum
- `properties` (Properties): Betöltött properties objektum

### 3. Connection Interface

#### `IDBConnection`
Interface a connection objektumokhoz (file-ban van, részletes implementáció nincs beolvasva).

**Package:** `ac4y.base.template`

**Várható felelősség:**
- Connection contract definiálása
- Mockol hatóság teszteléshez
- Függőség injektálás támogatása

## Függőségek

### Maven Függőség

```xml
<dependency>
    <groupId>ac4y</groupId>
    <artifactId>ac4y-base</artifactId>
    <version>1.0.0</version>
</dependency>
```

**Tranzitív függőségek:**
- ac4y-utility (1.0.0) - ac4y-base-n keresztül

**JDBC Driver függőség (külső, nem beágyazott):**

Az alkalmazásnak magának kell biztosítania a megfelelő JDBC driver-t:

```xml
<!-- MySQL példa -->
<dependency>
    <groupId>mysql</groupId>
    <artifactId>mysql-connector-java</artifactId>
    <version>8.0.33</version>
</dependency>

<!-- PostgreSQL példa -->
<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
    <version>42.6.0</version>
</dependency>

<!-- Oracle példa -->
<dependency>
    <groupId>com.oracle.database.jdbc</groupId>
    <artifactId>ojdbc8</artifactId>
    <version>21.9.0.0</version>
</dependency>
```

## Adatbázis Támogatás

A modul bármilyen JDBC-kompatibilis adatbázissal működik:

- **MySQL / MariaDB**
- **PostgreSQL**
- **Oracle**
- **Microsoft SQL Server**
- **H2 Database** (in-memory testing)
- **SQLite**
- Bármilyen JDBC driver-rel rendelkező adatbázis

## Tipikus Használati Minták

### 1. Egyszerű Lekérdezés

```java
try {
    // Connection létrehozása
    DBConnection dbConnection = new DBConnection("myapp.properties");
    Connection conn = dbConnection.getConnection();

    // Query végrehajtása
    PreparedStatement ps = conn.prepareStatement("SELECT * FROM users WHERE id = ?");
    ps.setInt(1, 123);
    ResultSet rs = ps.executeQuery();

    while (rs.next()) {
        String name = rs.getString("name");
        String email = rs.getString("email");
        // feldolgozás
    }

    // Cleanup
    rs.close();
    ps.close();
    conn.close();

} catch (SQLException | ClassNotFoundException | IOException | Ac4yException e) {
    ErrorHandler.addStack(e);
}
```

### 2. Insert Művelet

```java
try {
    DBConnection dbConnection = new DBConnection();
    Connection conn = dbConnection.getConnection();

    PreparedStatement ps = conn.prepareStatement(
        "INSERT INTO users (name, email) VALUES (?, ?)"
    );
    ps.setString(1, "John Doe");
    ps.setString(2, "john@example.com");

    int rowsAffected = ps.executeUpdate();
    System.out.println("Inserted " + rowsAffected + " rows");

    ps.close();
    conn.close();

} catch (Exception e) {
    ErrorHandler.addStack(e);
}
```

### 3. Transaction Management

```java
Connection conn = null;
try {
    DBConnection dbConnection = new DBConnection("db.properties");
    conn = dbConnection.getConnection();

    // Transaction kezdése
    conn.setAutoCommit(false);

    // Több műveletet
    PreparedStatement ps1 = conn.prepareStatement("UPDATE accounts SET balance = balance - ? WHERE id = ?");
    ps1.setDouble(1, 100.0);
    ps1.setInt(2, 1);
    ps1.executeUpdate();

    PreparedStatement ps2 = conn.prepareStatement("UPDATE accounts SET balance = balance + ? WHERE id = ?");
    ps2.setDouble(1, 100.0);
    ps2.setInt(2, 2);
    ps2.executeUpdate();

    // Commit
    conn.commit();

    ps1.close();
    ps2.close();

} catch (Exception e) {
    if (conn != null) {
        try {
            conn.rollback(); // Hiba esetén rollback
        } catch (SQLException ex) {
            ErrorHandler.addStack(ex);
        }
    }
    ErrorHandler.addStack(e);
} finally {
    if (conn != null) {
        try {
            conn.setAutoCommit(true);
            conn.close();
        } catch (SQLException e) {
            ErrorHandler.addStack(e);
        }
    }
}
```

### 4. Adapter Pattern Használat

```java
try {
    DBConnection dbConnection = new DBConnection("app.properties");
    Connection conn = dbConnection.getConnection();

    // Adapter létrehozása
    Ac4yDBAdapter adapter = new Ac4yDBAdapter(conn);

    // Átadás más komponensnek
    myService.setDatabaseAdapter(adapter);
    myService.processData();

    // Cleanup
    adapter.getConnection().close();

} catch (Exception e) {
    ErrorHandler.addStack(e);
}
```

### 5. Connection Pooling Előkészítés

```java
// Egyszerű connection létrehozás
DBConnection dbConn1 = new DBConnection("pool.properties");
DBConnection dbConn2 = new DBConnection("pool.properties");

// Több connection kezelése
List<Connection> connections = new ArrayList<>();
connections.add(dbConn1.getConnection());
connections.add(dbConn2.getConnection());

// Használat...

// Cleanup
for (Connection c : connections) {
    c.close();
}
```

**Megjegyzés:** Éles connection pooling-hoz használd az `ac4y-connection-pool` modult!

## AI Agent Használati Útmutató

### Gyors Döntési Fa

**Kérdés:** Mire van szükséged?

1. **Egyszerű JDBC Connection** → `DBConnection`
   - Properties fájlból? → `new DBConnection("mydb.properties")`
   - Default properties? → `new DBConnection()`
   - Paraméterekkel? → `new DBConnection(driver, connStr, host, user, pass)`

2. **Connection tárolása/átadása** → `Ac4yDBAdapter`
   - Wrapper kell? → `new Ac4yDBAdapter(connection)`

3. **Connection Interface** → `IDBConnection`
   - Mock teszteléshez? → Implementáld IDBConnection-t
   - Függőség injektáláshoz? → Használd IDBConnection interface-t

### Token-hatékony Tudás

**Mit tartalmaz:**
- JDBC connection létrehozás
- Properties-alapú konfiguráció
- Connection wrapper (adapter)

**Mit NEM tartalmaz:**
- Connection pooling (→ ac4y-connection-pool)
- Query builder
- ORM funkcionalitás
- Transaction manager

**Függőségek:**
- ac4y-base (1.0.0)
- ExternalPropertyHandler használata

**Kivételek:**
- ClassNotFoundException (driver nem található)
- SQLException (connection/query hiba)
- IOException (properties fájl hiba)
- Ac4yException (konfiguráció hiba)

## Properties Fájl Példák

### MySQL Konfiguráció
```properties
# mysql.properties
driver=com.mysql.cj.jdbc.Driver
connectionString=jdbc:mysql://localhost:3306/mydb?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
dbuser=root
dbpassword=mypassword
```

### PostgreSQL Konfiguráció
```properties
# postgresql.properties
driver=org.postgresql.Driver
connectionString=jdbc:postgresql://localhost:5432/mydb
dbuser=postgres
dbpassword=pgpassword
```

### H2 In-Memory (Testing)
```properties
# h2-test.properties
driver=org.h2.Driver
connectionString=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1
dbuser=sa
dbpassword=
```

### Oracle Konfiguráció
```properties
# oracle.properties
driver=oracle.jdbc.driver.OracleDriver
connectionString=jdbc:oracle:thin:@localhost:1521:xe
dbuser=system
dbpassword=oracle
```

## Build és Telepítés

```bash
# Build
mvn clean install

# Test
mvn test

# Deploy to GitHub Packages
mvn deploy
```

**GitHub Packages:**
```xml
<dependency>
    <groupId>ac4y</groupId>
    <artifactId>ac4y-database</artifactId>
    <version>1.0.0</version>
</dependency>
```

## Best Practices

1. **Mindig zárd le a connection-öket** finally blokkban vagy try-with-resources-szal
2. **Properties fájlok** nevezzétek el a modul szerint (module.properties)
3. **Ne hard-code-old jelszavakat** mindig properties-ből töltsd
4. **Transaction használat** fontos műveletsorozatoknál
5. **Connection pool** használata ajánlott éles környezetben (ac4y-connection-pool)
6. **JDBC driver** dependency-t az alkalmazás pom.xml-jében add meg, ne a library-ben

## Továbbfejlesztési Lehetőségek

- Connection pool integráció
- Retry mechanizmus connection hibánál
- Health check support
- Metrics gyűjtés
- Connection timeout konfigurálhatóság
