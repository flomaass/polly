package polly.rx.core.orion.model.json;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import polly.rx.core.orion.model.DefaultFleet;
import polly.rx.core.orion.model.DefaultPortal;
import polly.rx.core.orion.model.DefaultSector;
import polly.rx.core.orion.model.Fleet;
import polly.rx.core.orion.model.Portal;
import polly.rx.core.orion.model.PortalType;
import polly.rx.core.orion.model.Production;
import polly.rx.core.orion.model.Sector;
import polly.rx.core.orion.model.SectorType;
import polly.rx.parsing.VenadHelper;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.reflect.TypeToken;

import de.skuzzle.polly.tools.Check;

class SectorJsonHandler extends AbstractJsonHandler implements JsonSerializer<Sector>,
        JsonDeserializer<FromClientSector> {

    private final static String QUAD_NAME = "quadName"; //$NON-NLS-1$
    private final static String X = "x"; //$NON-NLS-1$
    private final static String Y = "y"; //$NON-NLS-1$
    private final static String IMG_NAME = "imgName"; //$NON-NLS-1$
    private final static String TYPE_NAME = "type"; //$NON-NLS-1$
    private final static String ATTACKER = "attacker"; //$NON-NLS-1$
    private final static String DEFENDER = "defender"; //$NON-NLS-1$
    private final static String GUARD = "guard"; //$NON-NLS-1$
    private final static String DATE = "date"; //$NON-NLS-1$
    private final static String PRODUCTION = "production"; //$NON-NLS-1$
    private final static String OWN_FLEETS = "ownFleets"; //$NON-NLS-1$
    private final static String FLEETS = "fleets"; //$NON-NLS-1$
    private final static String CLAN_PORTALS = "clanPortals"; //$NON-NLS-1$
    private final static String PERSONAL_PORTALS = "personalPortals"; //$NON-NLS-1$
    private final static String SHARE_FLEET_POSITION = "shareOwnFleets"; //$NON-NLS-1$
    private final static String SELF = "self"; //$NON-NLS-1$
    
    private final static String REVORIX_ID = "fleetId"; //$NON-NLS-1$
    private final static String FLEET_NAME = "fleetName"; //$NON-NLS-1$
    private final static String OWNER = "owner"; //$NON-NLS-1$
    
    final static Type CLIENT_SECTOR_TYPE = new TypeToken<FromClientSector>() {}.getType();
    final static Type SECTOR_TYPE = new TypeToken<Sector>() {}.getType();
    
    
    private final ProductionJsonHandler productionHandler;



    public SectorJsonHandler(ProductionJsonHandler productionHandler) {
        Check.objects(productionHandler).notNull();
        this.productionHandler = productionHandler;
    }



    private DateFormat getDateFormat() {
        return new SimpleDateFormat("dd.MM.yyyy HH:mm"); //$NON-NLS-1$
    }



    @Override
    public FromClientSector deserialize(JsonElement json, Type typeOfT,
            JsonDeserializationContext context) throws JsonParseException {
        final JsonObject obj = json.getAsJsonObject();
        
        final String self = this.getMemberOrThrow(obj, SELF).getAsString();
        final String quadName = this.getMemberOrThrow(obj, QUAD_NAME).getAsString(); 
        final int x = this.getMemberOrThrow(obj, X).getAsInt(); 
        final int y = this.getMemberOrThrow(obj, Y).getAsInt(); 
        final int attacker = this.getMemberOrThrow(obj, ATTACKER).getAsInt(); 
        final int defender = this.getMemberOrThrow(obj, DEFENDER).getAsInt(); 
        final int guard = this.getMemberOrThrow(obj, GUARD).getAsInt(); 
        final String typeName = this.getMemberOrDefault(obj, 
                TYPE_NAME, new JsonPrimitive("")).getAsString();  //$NON-NLS-1$
        final SectorType type = SectorType.byName(typeName);
        
        final JsonArray production = this.getMemberOrDefault(obj, 
                PRODUCTION, new JsonArray()).getAsJsonArray();
        final List<Production> productionC = new ArrayList<>(production.size());
        for (int i = 0; i < production.size(); ++i) {
            final Production prod = this.productionHandler.deserialize(production.get(i), 
                    ProductionJsonHandler.PRODUCTION_TYPE, context);
            productionC.add(prod);
        }
        final boolean shareFleets = this.getMemberOrDefault(obj, SHARE_FLEET_POSITION, 
                new JsonPrimitive(true)).getAsBoolean();
        
        final Sector s = new DefaultSector(quadName, x, y, attacker, defender, guard, 
                type, productionC);
        
        final List<Fleet> ownFleets = this.readFleets(
                s, obj.get(OWN_FLEETS));
        final List<Fleet> fleets = this.readFleets(s, obj.get(FLEETS));
        final List<Portal> personal = this.readPortals(s, PortalType.PRIVATE, 
                obj.get(PERSONAL_PORTALS));
        final List<Portal> clan = this.readPortals(s, PortalType.CLAN, 
                obj.get(CLAN_PORTALS));
        
        return new FromClientSector(s, self, null, shareFleets, ownFleets, 
                fleets, clan, personal);
    }
    
    
    
    private List<Fleet> readFleets(Sector s, JsonElement element) {
        if (element == null) {
            return Collections.emptyList();
        }
        final JsonArray array = element.getAsJsonArray();
        final List<Fleet> result = new ArrayList<>(array.size());
        for (int i = 0; i < array.size(); ++i) {
            final JsonObject obj = array.get(i).getAsJsonObject();
            
            // id will be -1 if no id is present,
            final int rxId = this.getMemberOrDefault(
                    obj, REVORIX_ID, new JsonPrimitive(-1)).getAsInt();
            final String fleetName = obj.get(FLEET_NAME).getAsString();
            final String owner = obj.get(OWNER).getAsString();
            final String ownerName = VenadHelper.getName(owner);
            final String ownerClan = VenadHelper.getClan(owner);
            result.add(new DefaultFleet(rxId, fleetName, ownerName, ownerClan, s));
        }
        return result;
    }
    
    
    
    private List<Portal> readPortals(Sector s, PortalType type, JsonElement element) {
        if (element == null) {
            return Collections.emptyList();
        }
        final JsonArray array = element.getAsJsonArray();
        final List<Portal> result = new ArrayList<>(array.size());
        for (int i = 0; i < array.size(); ++i) {
            final String name = array.get(i).getAsString();
            final String ownerName = VenadHelper.getName(name);
            final String ownerClan = VenadHelper.getClan(name);
            result.add(new DefaultPortal(s, ownerName, ownerClan, type));
        }
        return result;
    }



    @Override
    public JsonElement serialize(Sector src, Type typeOfSrc,
            JsonSerializationContext context) {
        final JsonObject obj = new JsonObject();
        obj.addProperty(QUAD_NAME, src.getQuadName());
        obj.addProperty(X, src.getX());
        obj.addProperty(Y, src.getY());
        obj.addProperty(IMG_NAME, src.getType().getImgName());
        obj.addProperty(TYPE_NAME, src.getType().toString());
        obj.addProperty(ATTACKER, src.getAttackerBonus());
        obj.addProperty(DEFENDER, src.getDefenderBonus());
        obj.addProperty(GUARD, src.getSectorGuardBonus());
        obj.addProperty(DATE, this.getDateFormat().format(src.getDate()));
        final JsonArray production = new JsonArray();
        for (final Production prod : src.getRessources()) {
            production.add(this.productionHandler.serialize(
                    prod, ProductionJsonHandler.PRODUCTION_TYPE, context));
        }
        obj.add(PRODUCTION, production);
        return obj;
    }
}
