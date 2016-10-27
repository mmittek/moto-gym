function [x,y,z, mx,my,mz,idx99] = gpsToXYZ(lat, lon, alt)
    lat = lat.*pi/180;
    lon = lon.*pi/180;

    r = 6371000 + alt;
    
%    r = r*0.000621371;
    
    x = r.*cos( lat ).*cos( lon );
    y = r.*cos( lat ).*sin( lon );
    z = r.*sin( lat );
    
    x = x-x(1);
    y = y-y(1);
    z = z-z(1);
    
    mx = mean(x);
    my = mean(y);
    mz = mean(z);
    d = sqrt(sum(([x,y,z]-repmat([mx,my,mz], [numel(x),1])).^2,2));
    idx99 = find(d < 3*std(d));
end