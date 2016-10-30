init;
if ~exist('dataLoaded')
    csvFilename = '/Users/mmittek/Desktop/20160929_224814_132_90.csv';
    data = loadCSVAndPreprocess(csvFilename);
   % data = data(2204:end,:);
    data(:,1) = data(:,1) - data(1,1);
    
    acc = data(:,[ACCX,ACCY,ACCZ]);
    gyro = data(:,[GYROX,GYROY,GYROZ]);
end

box = makeBox(0.5,1,4);
angle = zeros( size(data,1),3 );
grav = zeros( size(data,1),3 );
orient_gyro = zeros( size(data,1),3 );
alpha = 0.999;

for i=2:size(data,1)
    dt = (data(i,1)-data(i-1,1));
    
        beta = alpha - dt/1000;
        grav(i,:) = grav(i-1,:)*beta + (1-beta)*acc(i,:);
        grav(i,:) = grav(i,:)./norm( grav(i,:) );
    
        angle(i,:) = angle(i-1,:) + gyro(i,:)*dt/1000;
        
        R = rotXYZ( angle(i,1), angle(i,2), angle(i,3) );
        orient_gyro(i,:) = [R(1,2) ,R(2,2),R(3,2)];
        
%         if(mod(i,10)==0)
%             R = rotXYZ( angle(i,1), angle(i,2), angle(i,3) );
%             boxr = (R*box')';
%             figure(1)
%             mag = norm(angle(i,:));
%             plot3( boxr(:,1), boxr(:,2), boxr(:,3) , 'r-o');
%             axis equal;
%             axis([-1,1,-1,1,-1,1]);
%             grid on;
%             drawnow;
%         end
    
end

linacc = acc - grav;

figure(2)
for i=1:10:size(data,1)

    R = rotXYZ( angle(i,1), angle(i,2), angle(i,3) );
    boxr = (R*box')';
    
    plot(1,1);
    plotVec3D( orient_gyro(i,1) ,orient_gyro(i,2),orient_gyro(i,3));
    plotVec3D( grav(i,1) , grav(i,2) , grav(i,3) );
    hold on;
    plot3( boxr(:,1), boxr(:,2), boxr(:,3), 'b-o' );

%plotVec3D( orient_gyro(i,1)-grav(i,1) ,orient_gyro(i,2)-grav(i,2),orient_gyro(i,3)-grav(i,3));

    hold off;
    view(3);
    axis equal;
    axis([-1,1,-1,1,-1,1]);
    grid on;
    drawnow;

end

