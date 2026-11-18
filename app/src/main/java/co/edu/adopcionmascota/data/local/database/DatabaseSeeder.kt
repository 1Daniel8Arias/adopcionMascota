package co.edu.adopcionmascota.data.local.database

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import co.edu.adopcionmascota.data.local.mapper.toEntity

// Inserta los datos de ejemplo dentro de RoomDatabase.Callback.onCreate.
// Se hace de forma síncrona con la conexión que Room está creando, así la primera consulta
// (por ejemplo, el login) ya encuentra los datos. onCreate solo corre una vez.
object DatabaseSeeder {

    fun seed(db: SupportSQLiteDatabase) {
        val now = System.currentTimeMillis()

        SeedData.users(now).map { it.toEntity() }.forEach { u ->
            db.insert("users", SQLiteDatabase.CONFLICT_REPLACE, ContentValues().apply {
                put("id", u.id); put("name", u.name); put("city", u.city); put("address", u.address)
                put("email", u.email); put("password", u.password); put("phoneNumber", u.phoneNumber)
                put("profilePictureUrl", u.profilePictureUrl); put("role", u.role); put("bio", u.bio)
                put("isTemporaryHome", u.isTemporaryHome); put("lostPetAlerts", u.lostPetAlerts)
                put("showPublicPhone", u.showPublicPhone); put("points", u.points); put("createdAt", u.createdAt)
            })
        }

        SeedData.publications(now).map { it.toEntity() }.forEach { p ->
            db.insert("publications", SQLiteDatabase.CONFLICT_REPLACE, ContentValues().apply {
                put("id", p.id); put("name", p.name); put("description", p.description)
                put("location", p.location); put("status", p.status); put("type", p.type); put("image", p.image)
                put("createdAt", p.createdAt); put("distancia", p.distancia); put("likes", p.likes)
                put("age", p.age); put("gender", p.gender); put("species", p.species); put("breed", p.breed)
                put("size", p.size); put("isVaccinated", p.isVaccinated); put("isDewormed", p.isDewormed)
                put("isSterilized", p.isSterilized); put("hasMicrochip", p.hasMicrochip)
                put("latitude", p.latitude); put("longitude", p.longitude); put("ownerId", p.ownerId)
                put("rejectionReason", p.rejectionReason); put("reviewedAt", p.reviewedAt)
            })
        }

        SeedData.comments(now).map { it.toEntity() }.forEach { c ->
            db.insert("comments", SQLiteDatabase.CONFLICT_REPLACE, ContentValues().apply {
                put("id", c.id); put("publicationId", c.publicationId); put("authorId", c.authorId)
                put("author", c.author); put("authorTag", c.authorTag); put("text", c.text); put("type", c.type)
                put("createdAt", c.createdAt); put("thanks", c.thanks); put("thankedByMe", c.thankedByMe)
            })
        }

        SeedData.notifications(now).map { it.toEntity() }.forEach { n ->
            db.insert("notifications", SQLiteDatabase.CONFLICT_REPLACE, ContentValues().apply {
                put("id", n.id); put("userId", n.userId); put("type", n.type); put("category", n.category)
                put("title", n.title); put("message", n.message); put("createdAt", n.createdAt)
                put("publicationId", n.publicationId); put("referenceId", n.referenceId)
                put("actionLabel", n.actionLabel); put("isRead", n.isRead)
            })
        }
    }
}
