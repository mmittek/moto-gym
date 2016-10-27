function [x,y,z] = gpsToXYZ(lat, lon, alt)
    lat = lat.*pi/180;
    lon = lon.*pi/180;

    r = 6371000 + alt;
    
    r = r.*0.000621371;
    
    x = r.*cos( lat ).*cos( lon );
    y = r.*cos( lat ).*sin( lon );
    z = r.*sin( lat );
end