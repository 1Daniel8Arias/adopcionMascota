# Cuentas de prueba (Firebase)

La app las crea sola la primera vez que se abre una compilación **debug**
(`data/remote/sync/FirebaseSeeder.kt`), junto con las publicaciones, comentarios
y notificaciones de ejemplo. Los correos usan el dominio reservado `.test`, así que
el correo de recuperación de contraseña no llega a ninguna bandeja real.

| Usuario          | Correo                   | Contraseña     | Rol (users/{uid}.role) |
|------------------|--------------------------|----------------|------------------------|
| Santiago López   | santiago@patitas.test    | Santiago123    | USER                   |
| Valentina Gómez  | valentina@patitas.test   | Valentina123   | USER                   |
| Andrés Martínez  | andres@patitas.test      | Andres123      | MODERATOR              |

Para probar la recuperación de contraseña de verdad, registra una cuenta con un correo
propio y usa "¿Olvidaste tu contraseña?".

Si el seeder falla (Authentication o Firestore sin activar, o reglas que lo impiden),
el error queda en Logcat con la etiqueta `FirebaseSeeder` y se reintenta en el siguiente
arranque. Para volver a sembrar hay que borrar el documento `meta/seed` y los datos de la app.

## Reglas sugeridas para desarrollo (solo usuarios autenticados)

Firestore:

```
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    match /{document=**} {
      allow read, write: if request.auth != null;
    }
  }
}
```

Storage:

```
rules_version = '2';
service firebase.storage {
  match /b/{bucket}/o {
    match /{allPaths=**} {
      allow read, write: if request.auth != null;
    }
  }
}
```
